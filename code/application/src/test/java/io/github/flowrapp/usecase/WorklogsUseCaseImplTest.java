package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.model.value.WorklogClockInRequest;
import io.github.flowrapp.model.value.WorklogClockOutRequest;
import io.github.flowrapp.model.value.WorklogFilteredRequest;
import io.github.flowrapp.model.value.WorklogUpdateRequest;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;

import lombok.val;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class WorklogsUseCaseImplTest {

  @Mock
  private WorklogRepositoryOutput worklogRepositoryOutput;

  @Mock
  private BusinessRepositoryOutput businessRepositoryOutput;

  @Mock
  private BusinessUserRepositoryOutput businessUserRepositoryOutput;

  @Mock
  private UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private WorklogsUseCaseImpl worklogsUseCase;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockIn_success(WorklogClockInRequest request, User currentUser, BusinessUser businessUser, Worklog savedWorklog) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessUserRepositoryOutput.getByUserAndBusinessId(currentUser.id(), request.businessId()))
        .thenReturn(Optional.of(businessUser));
    when(worklogRepositoryOutput.save(any(Worklog.class))).thenReturn(savedWorklog);

    // When
    val result = worklogsUseCase.clockIn(request);

    // Then
    assertThat(result).isEqualTo(savedWorklog);
    verify(worklogRepositoryOutput).save(argThat(worklog -> worklog.user().equals(businessUser.user())
        &&
        worklog.business().equals(businessUser.business()) &&
        worklog.clockIn().equals(request.clockIn()) &&
        worklog.clockOut() == null));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockIn_businessNotFound(WorklogClockInRequest request, User currentUser) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessUserRepositoryOutput.getByUserAndBusinessId(currentUser.id(), request.businessId()))
        .thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.clockIn(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockOut_success(WorklogClockOutRequest request, User currentUser, Worklog openWorklog) {
    // Given
    openWorklog = openWorklog.withUser(currentUser).withClockOut(null);
    request = request.toBuilder().clockOut(openWorklog.clockIn().plus(3, ChronoUnit.HOURS)).build();

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId()))
        .thenReturn(Optional.of(openWorklog));
    when(worklogRepositoryOutput.save(argThat(argument -> Objects.equals(argument.user().id(), currentUser.id()))))
        .then(returnsFirstArg());

    // When
    val result = worklogsUseCase.clockOut(request);

    // Then
    assertThat(result)
        .isNotNull()
        .returns(currentUser.id(), worklog -> worklog.user().id());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockOut_worklogNotFound(WorklogClockOutRequest request, User currentUser) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId())).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.clockOut(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockOut_notOwner(WorklogClockOutRequest request, User currentUser, Worklog worklog) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId())).thenReturn(Optional.of(worklog));

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.clockOut(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockOut_alreadyClosed(WorklogClockOutRequest request, User currentUser, Worklog worklog) {
    // Given
    worklog = worklog.withUser(currentUser);

    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId())).thenReturn(Optional.of(worklog));

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.clockOut(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockOut_invalidWorklog(WorklogClockOutRequest request, User currentUser, Worklog openWorklog) {
    // Given
    request = request.toBuilder().clockOut(openWorklog.clockIn().plus(3, ChronoUnit.HOURS)).build();
    openWorklog = openWorklog.withUser(currentUser).withClockOut(null);

    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId())).thenReturn(Optional.of(openWorklog));

    // When / Then
    WorklogClockOutRequest finalRequest = request;
    assertThatThrownBy(() -> worklogsUseCase.clockOut(finalRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updateWorklog_success(WorklogUpdateRequest request, User currentUser, Worklog existingWorklog) {
    // Given
    request = request.toBuilder().clockOut(request.clockIn().plus(3, ChronoUnit.HOURS)).build();
    var existingWorklog2 = existingWorklog.withUser(currentUser);

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId()))
        .thenReturn(Optional.of(existingWorklog2));
    when(worklogRepositoryOutput.save(argThat(argument -> Objects.equals(argument.id(), existingWorklog2.id()))))
        .then(returnsFirstArg());

    // When
    val result = worklogsUseCase.updateWorklog(request);

    // Then
    assertThat(result)
        .isNotNull()
        .returns(existingWorklog.id(), Worklog::id);
    verify(applicationEventPublisher).publishEvent(any());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updateWorklog_worklogNotFound(WorklogUpdateRequest request, User currentUser) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId())).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.updateWorklog(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updateWorklog_notOwner(WorklogUpdateRequest request, User currentUser, Worklog worklog) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId())).thenReturn(Optional.of(worklog));

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.updateWorklog(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updateWorklog_invalidWorklog(User currentUser, Worklog existingWorklog) {
    // Given
    var request = WorklogUpdateRequest.builder()
        .worklogId(existingWorklog.id())
        .clockIn(Instant.now())
        .clockOut(Instant.now().plus(3, ChronoUnit.HOURS)) // Invalid clock out
        .build();

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(request.worklogId()))
        .thenReturn(Optional.of(existingWorklog));

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.updateWorklog(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getById_success(Integer worklogId, User currentUser, Worklog worklog) {
    // Given
    worklog = worklog.withUser(currentUser);

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(worklogId))
        .thenReturn(Optional.of(worklog));

    // When
    val result = worklogsUseCase.getById(worklogId);

    // Then
    assertThat(result)
        .isNotNull()
        .returns(worklog.id(), Worklog::id);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getById_worklogNotFound(Integer worklogId, User currentUser) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(worklogId)).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.getById(worklogId))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getById_notOwner(Integer worklogId, User currentUser, Worklog worklog) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(worklogRepositoryOutput.findById(worklogId))
        .thenReturn(Optional.of(worklog));

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.getById(worklogId))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserWorklogs_success(WorklogFilteredRequest request, User currentUser, List<Worklog> worklogs) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(worklogRepositoryOutput.findAllFiltered(argThat(argument -> Objects.equals(argument.userId(), currentUser.id()))))
        .thenReturn(worklogs);

    // When
    val result = worklogsUseCase.getUserWorklogs(request);

    // Then
    assertThat(result).isEqualTo(worklogs);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessWorklogs_success(WorklogFilteredRequest request, User currentUser, Business business, List<Worklog> worklogs) {
    // Given
    business = business.toBuilder().owner(currentUser).build();

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId()))
        .thenReturn(Optional.of(business));
    when(worklogRepositoryOutput.findAllFiltered(argThat(argument -> Objects.equals(argument.businessId(), request.businessId()))))
        .thenReturn(worklogs);

    // When
    val result = worklogsUseCase.getBusinessWorklogs(request);

    // Then
    assertThat(result).isEqualTo(worklogs);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessWorklogs_businessNotFound(WorklogFilteredRequest request, User currentUser) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.getBusinessWorklogs(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessWorklogs_notOwner(WorklogFilteredRequest request, User currentUser, Business business) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId()))
        .thenReturn(Optional.of(business));

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.getBusinessWorklogs(request))
        .isInstanceOf(FunctionalException.class);
  }

}
