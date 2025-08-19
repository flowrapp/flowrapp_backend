package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.User;
import io.github.flowrapp.value.TimesheetFilterRequest;
import io.github.flowrapp.value.UserTimeReportSummary;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.ReportRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.service.TimesheetReportGeneratorService;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@DisplayName("TimesheetUseCaseImpl Tests")
class TimesheetUseCaseImplTest {

  @Mock
  private ReportRepositoryOutput reportRepositoryOutput;

  @Mock
  private UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Mock
  private BusinessRepositoryOutput businessRepositoryOutput;

  @Mock
  private TimesheetReportGeneratorService timesheetReportGeneratorService;

  @InjectMocks
  private TimesheetUseCaseImpl timesheetUseCase;

  @Nested
  @DisplayName("getSummaryReport")
  class GetSummaryReportTests {

    @ParameterizedTest
    @InstancioSource(samples = 20)
    @DisplayName("Should return summary report when user is business owner")
    void shouldReturnSummaryReportWhenUserIsBusinessOwner(TimesheetFilterRequest filter, User user, Business business,
        List<Report> reportList, List<UserTimeReportSummary> userTimeReportSummaries) {

      // GIVEN
      business = business.toBuilder().owner(user).build();

      when(userSecurityContextHolderOutput.getCurrentUser())
          .thenReturn(user);
      when(businessRepositoryOutput.findById(filter.businessId()))
          .thenReturn(Optional.of(business));
      when(reportRepositoryOutput.findAll(filter))
          .thenReturn(reportList);
      when(timesheetReportGeneratorService.computeWeeklyHoursReport(filter.from(), filter.to(), reportList))
          .thenReturn(userTimeReportSummaries);

      // WHEN
      var resultSummary = timesheetUseCase.getSummaryReport(filter);

      // THEN
      assertThat(resultSummary)
          .isEqualTo(userTimeReportSummaries);
    }

    @ParameterizedTest
    @InstancioSource(samples = 5)
    @DisplayName("Should throw exception when business not found")
    void shouldThrowExceptionWhenBusinessNotFound(TimesheetFilterRequest filter, User user) {

      // GIVEN
      when(userSecurityContextHolderOutput.getCurrentUser())
          .thenReturn(user);
      when(businessRepositoryOutput.findById(filter.businessId()))
          .thenReturn(Optional.empty());

      // WHEN & THEN
      assertThatThrownBy(() -> timesheetUseCase.getSummaryReport(filter))
          .isInstanceOf(FunctionalException.class)
          .extracting("code", "status", "message")
          .containsExactly(
              FunctionalError.BUSINESS_NOT_FOUND.getCode(),
              FunctionalError.BUSINESS_NOT_FOUND.getStatus(),
              FunctionalError.BUSINESS_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @InstancioSource(samples = 5)
    @DisplayName("Should throw exception when user is not business owner")
    void shouldThrowExceptionWhenUserIsNotBusinessOwner(TimesheetFilterRequest filter, User user, User otherUser, Business business) {

      // GIVEN
      business = business.toBuilder().owner(otherUser).build();

      when(userSecurityContextHolderOutput.getCurrentUser())
          .thenReturn(user);
      when(businessRepositoryOutput.findById(filter.businessId()))
          .thenReturn(Optional.of(business));

      // WHEN & THEN
      assertThatThrownBy(() -> timesheetUseCase.getSummaryReport(filter))
          .isInstanceOf(FunctionalException.class)
          .extracting("code", "status", "message")
          .containsExactly(
              FunctionalError.USER_NOT_OWNER_OF_BUSINESS.getCode(),
              FunctionalError.USER_NOT_OWNER_OF_BUSINESS.getStatus(),
              FunctionalError.USER_NOT_OWNER_OF_BUSINESS.getMessage());
    }
  }

  @Nested
  @DisplayName("getUserSummaryReport")
  class GetUserSummaryReportTests {

    @ParameterizedTest
    @InstancioSource(samples = 20)
    @DisplayName("Should return user summary report")
    void shouldReturnUserSummaryReport(TimesheetFilterRequest filter, User user,
        List<Report> reportList, List<UserTimeReportSummary> userTimeReportSummaries) {

      // GIVEN
      filter = filter.withUserId(user.id());
      when(userSecurityContextHolderOutput.getCurrentUser())
          .thenReturn(user);
      when(reportRepositoryOutput.findAll(filter))
          .thenReturn(reportList);
      when(timesheetReportGeneratorService.computeWeeklyHoursReport(filter.from(), filter.to(), reportList))
          .thenReturn(userTimeReportSummaries);

      // WHEN
      var result = timesheetUseCase.getUserSummaryReport(filter);

      // THEN
      assertThat(result)
          .isEqualTo(userTimeReportSummaries);
    }
  }
}
