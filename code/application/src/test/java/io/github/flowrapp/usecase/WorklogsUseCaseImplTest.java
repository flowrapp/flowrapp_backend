package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;
import io.github.flowrapp.utils.DateUtils;
import io.github.flowrapp.value.WorklogClockInRequest;
import io.github.flowrapp.value.WorklogClockOutRequest;
import io.github.flowrapp.value.WorklogFilteredRequest;
import io.github.flowrapp.value.WorklogUpdateRequest;

import lombok.val;
import org.instancio.Instancio;
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
        worklog.clockIn().equals(request.clockIn().atZoneSameInstant(businessUser.business().timezoneOffset()).toOffsetDateTime()) &&
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
    openWorklog = openWorklog.withUser(currentUser)
        .withClockIn(OffsetDateTime.now().minusNanos(30))
        .withClockOut(null);
    request = request.toBuilder()
        .clockOut(openWorklog.clockIn().plusNanos(30))
        .build();

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
  void clockOut_success_splitDay(WorklogClockOutRequest request, User currentUser, Worklog openWorklog) {
    // Given
    openWorklog = openWorklog.withUser(currentUser)
        .withClockIn(OffsetDateTime.now().minusDays(2))
        .withClockOut(null);
    request = request.toBuilder()
        .clockOut(openWorklog.clockIn().plusHours(23).plusMinutes(59))
        .build();

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
    verify(worklogRepositoryOutput, times(2)).save(any());
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
    request = request.toBuilder().clockOut(openWorklog.clockIn().minusHours(3)).build();
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
  void updateWorklog_success(User currentUser, Worklog existingWorklog) {
    // Given
    var request = this.createValidWorklogUpdate();
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
        .returns(existingWorklog2.id(), Worklog::id);
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
        .clockIn(OffsetDateTime.now())
        .clockOut(OffsetDateTime.now().plusHours(3)) // Invalid clock out
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
  void getUserWorklogs_success(WorklogFilteredRequest request, User currentUser, Business business, List<Worklog> worklogs) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId()))
        .thenReturn(Optional.of(business));
    when(worklogRepositoryOutput.findAllFiltered(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(currentUser.id(), WorklogFilteredRequest::userId)
        .returns(request.businessId(), WorklogFilteredRequest::businessId)
        .returns(DateUtils.toZone(business.timezoneOffset()).andThen(DateUtils.atStartOfDay).apply(request.date()),
            WorklogFilteredRequest::from)
        .returns(DateUtils.toZone(business.timezoneOffset()).andThen(DateUtils.atEndOfDay).apply(request.date()),
            WorklogFilteredRequest::to)
        .returns(request.date(), WorklogFilteredRequest::date))))
            .thenReturn(worklogs);

    // When
    val result = worklogsUseCase.getUserWorklogs(request);

    // Then
    assertThat(result).isEqualTo(worklogs);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserWorklogs_success_withNoDate(WorklogFilteredRequest request, User currentUser, Business business, List<Worklog> worklogs) {
    // Given
    var request2 = request.withDate(null);

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request2.businessId()))
        .thenReturn(Optional.of(business));
    when(worklogRepositoryOutput.findAllFiltered(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(currentUser.id(), WorklogFilteredRequest::userId)
        .returns(request2.businessId(), WorklogFilteredRequest::businessId)
        .returns(DateUtils.toZone(business.timezoneOffset()).apply(request2.from()),
            WorklogFilteredRequest::from)
        .returns(DateUtils.toZone(business.timezoneOffset()).apply(request2.to()),
            WorklogFilteredRequest::to))))
                .thenReturn(worklogs);

    // When
    val result = worklogsUseCase.getUserWorklogs(request2);

    // Then
    assertThat(result).isEqualTo(worklogs);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserWorklogs_businessNotFound(WorklogFilteredRequest request, User currentUser) {
    // Given
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId()))
        .thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> worklogsUseCase.getUserWorklogs(request))
        .isInstanceOf(FunctionalException.class);
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

  private WorklogUpdateRequest createValidWorklogUpdate() {
    var clockIn = Instancio.gen().temporal().offsetDateTime().max(OffsetDateTime.now().minusDays(2))
        .min(OffsetDateTime.now().minusDays(3)).get();

    return Instancio.of(WorklogUpdateRequest.class)
        .set(field(WorklogUpdateRequest::clockIn), clockIn)
        .generate(field(WorklogUpdateRequest::clockOut),
            gen -> gen.temporal().offsetDateTime().range(clockIn, clockIn.plusDays(1)))
        .create();
  }

}
