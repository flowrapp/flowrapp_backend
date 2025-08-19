package io.github.flowrapp.usecase;

import java.util.List;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.value.TimesheetFilterRequest;
import io.github.flowrapp.value.UserTimeReportSummary;
import io.github.flowrapp.port.input.TimesheetUseCase;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.ReportRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.service.TimesheetReportGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimesheetUseCaseImpl implements TimesheetUseCase {

  private final ReportRepositoryOutput reportRepositoryOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  private final BusinessRepositoryOutput businessRepositoryOutput;

  private final TimesheetReportGeneratorService timesheetReportGeneratorService;

  @Override
  public List<UserTimeReportSummary> getSummaryReport(TimesheetFilterRequest filter) {
    log.debug("Get summary report for filters: {}", filter);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val business = businessRepositoryOutput.findById(filter.businessId())
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    if (!business.isOwner(currentUser)) {
      log.warn("User {} is not authorized to access business {}", currentUser.mail(), business.id());
      throw new FunctionalException(FunctionalError.USER_NOT_OWNER_OF_BUSINESS);
    }

    log.debug("User {} is requesting summary report for business {} with filters: {}",
        currentUser.mail(), business.id(), filter);

    return timesheetReportGeneratorService.computeWeeklyHoursReport(filter.from(), filter.to(),
        reportRepositoryOutput.findAll(filter));
  }

  @Override
  public List<UserTimeReportSummary> getUserSummaryReport(TimesheetFilterRequest filter) {
    log.debug("Get user summary report for filters: {}", filter);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    log.debug("User {} is requesting user summary report with filters: {}", currentUser.mail(), filter);

    return timesheetReportGeneratorService.computeWeeklyHoursReport(filter.from(), filter.to(),
        reportRepositoryOutput.findAll(
            filter.withUserId(currentUser.id())));
  }

}
