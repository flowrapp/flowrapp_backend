package io.github.flowrapp.usecase;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;

import java.util.List;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.input.WorklogUseCase;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;
import io.github.flowrapp.utils.DateUtils;
import io.github.flowrapp.value.CreateWorklogEvent;
import io.github.flowrapp.value.WorklogClockInRequest;
import io.github.flowrapp.value.WorklogClockOutRequest;
import io.github.flowrapp.value.WorklogFilteredRequest;
import io.github.flowrapp.value.WorklogUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorklogsUseCaseImpl implements WorklogUseCase {

  private final WorklogRepositoryOutput worklogRepositoryOutput;

  private final BusinessRepositoryOutput businessRepositoryOutput;

  private final BusinessUserRepositoryOutput businessUserRepositoryOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public Worklog clockIn(WorklogClockInRequest request) {
    log.debug("Clocking in worklog: {}", request);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val businessUser = businessUserRepositoryOutput.getByUserAndBusinessId(currentUser.id(), request.businessId())
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    log.debug("User {} is clockIn {} for business {}", currentUser.mail(), request.clockIn(), request.businessId());
    return worklogRepositoryOutput.save(
        Worklog.fromBusinessUser(businessUser, request.clockIn()));
  }

  @Transactional
  @Override
  public Worklog clockOut(WorklogClockOutRequest request) {
    log.debug("Clocking out worklog: {}", request);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val worklog = worklogRepositoryOutput.findById(request.worklogId())
        .orElseThrow(() -> new FunctionalException(FunctionalError.WORKLOG_NOT_FOUND));

    if (!worklog.isOwner(currentUser)) {
      log.warn("User {} is not the owner of worklog {}", currentUser.mail(), request.worklogId());
      throw new FunctionalException(FunctionalError.WORKLOG_NOT_OWNER);
    }

    if (worklog.isClosed()) {
      log.warn("Worklog {} is already closed", request.worklogId());
      throw new FunctionalException(FunctionalError.WORKLOG_CLOSED);
    }

    val updatedWorklog = worklog.withClockOut(request.clockOut()).toBusinessZone();
    if (!updatedWorklog.isValid()) {
      log.warn("Worklog {} is not valid after clocking out", request.worklogId());
      throw new FunctionalException(FunctionalError.WORKLOG_NOT_VALID);
    }

    this.checkOverlap(updatedWorklog);

    return this.splitWorklogByDay(updatedWorklog).stream() // Split by day, and return last clocked out worklog
        .map(this::saveClockOutWorklog)
        .reduce(maxBy(comparing(Worklog::clockOut)))
        .orElse(worklog); // Should not happen, but just in case
  }

  private List<Worklog> splitWorklogByDay(Worklog worklog) {
    if (worklog.clockIn().getDayOfWeek() == worklog.clockOut().getDayOfWeek())
      return List.of(worklog); // No need to split if it's the same day

    log.debug("Splitting worklog {} by day", worklog);

    val first = worklog
        .withClockOut(DateUtils.atEndOfDay.apply(worklog.clockIn())); // Set clock out at the end of day
    val second = worklog
        .withId(null) // Create a new worklog for the next day
        .withClockIn(DateUtils.atStartOfDay.apply(worklog.clockOut())) // Set clock in at the start of next day
        .withClockOut(worklog.clockOut());

    return List.of(first, second);
  }

  private Worklog saveClockOutWorklog(Worklog worklog) {
    log.debug("User {} is clockOut {} for worklog {}", worklog.user().mail(), worklog.clockOut(), worklog);
    worklog = worklogRepositoryOutput.save(worklog);

    applicationEventPublisher.publishEvent(
        CreateWorklogEvent.created(worklog)); // Publish event for further processing

    return worklog;
  }

  @Override
  public Worklog updateWorklog(WorklogUpdateRequest request) {
    log.debug("Updating worklog: {}", request);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val worklog = worklogRepositoryOutput.findById(request.worklogId())
        .orElseThrow(() -> new FunctionalException(FunctionalError.WORKLOG_NOT_FOUND));

    if (!worklog.isOwner(currentUser)) {
      log.warn("User {} is not the owner of worklog {}", currentUser.mail(), request.worklogId());
      throw new FunctionalException(FunctionalError.WORKLOG_NOT_OWNER);
    }

    val updatedWorklog = worklog.toBuilder()
        .clockIn(request.clockIn() != null ? request.clockIn() : worklog.clockIn())
        .clockOut(request.clockOut() != null ? request.clockOut() : worklog.clockOut())
        .build()
        .toBusinessZone();

    if (!updatedWorklog.isValid()) {
      log.warn("Worklog {} is not valid after update", request.worklogId());
      throw new FunctionalException(FunctionalError.WORKLOG_NOT_VALID);
    }

    this.checkOverlap(updatedWorklog);

    worklogRepositoryOutput.save(updatedWorklog);
    applicationEventPublisher.publishEvent(
        CreateWorklogEvent.updated(updatedWorklog, worklog)); // Publish event for further processing

    return updatedWorklog;
  }

  private void checkOverlap(Worklog worklog) {
    if (worklogRepositoryOutput.doesOverlap(worklog)) {
      log.warn("Worklog {} overlaps with existing worklogs", worklog.id());
      throw new FunctionalException(FunctionalError.WORKLOG_OVERLAP);
    }
  }

  @Override
  public Worklog getById(Integer worklogId) {
    log.debug("Retrieving worklog by ID: {}", worklogId);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val worklog = worklogRepositoryOutput.findById(worklogId)
        .orElseThrow(() -> new FunctionalException(FunctionalError.WORKLOG_NOT_FOUND));

    if (!worklog.isOwner(currentUser)) {
      log.warn("User {} is not the owner of worklog {}", currentUser.mail(), worklogId);
      throw new FunctionalException(FunctionalError.WORKLOG_NOT_OWNER);
    }

    return worklog;
  }

  @Override
  public List<Worklog> getUserWorklogs(WorklogFilteredRequest worklogFilteredRequest) {
    log.debug("Retrieving user worklogs with filter: {}", worklogFilteredRequest);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();

    return worklogRepositoryOutput.findAllFiltered(
        worklogFilteredRequest
            .truncate()
            .withUserId(currentUser.id()));
  }

  @Override
  public List<Worklog> getBusinessWorklogs(WorklogFilteredRequest worklogFilteredRequest) {
    log.debug("Retrieving business worklogs with filter: {}", worklogFilteredRequest);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val business = businessRepositoryOutput.findById(worklogFilteredRequest.businessId())
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    if (!business.isOwner(currentUser)) {
      log.warn("User {} is not the owner of business {}", currentUser.mail(), worklogFilteredRequest.businessId());
      throw new FunctionalException(FunctionalError.USER_NOT_OWNER_OF_BUSINESS);
    }

    return worklogRepositoryOutput.findAllFiltered(
        worklogFilteredRequest.truncate());
  }

}
