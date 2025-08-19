package io.github.flowrapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.UserTimeReportSummary;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@DisplayName("TimesheetReportGeneratorService Tests")
class TimesheetReportGeneratorServiceTest {

  @InjectMocks
  private TimesheetReportGeneratorService timesheetReportGeneratorService;

  @Nested
  @DisplayName("computeWeeklyHoursReport")
  class ComputeWeeklyHoursReportTests {

    @Test
    @DisplayName("Should return empty list when no reports provided")
    void shouldReturnEmptyListWhenNoReports() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      List<Report> reports = List.of();

      // When
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should compute report for single user with single report")
    void shouldComputeReportForSingleUserWithSingleReport() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      LocalDate reportDay = LocalDate.of(2024, 1, 16);
      BigDecimal hours = new BigDecimal("8.0");

      User user = Instancio.of(User.class)
          .set(field(User::id), 1)
          .set(field(User::name), "John Doe")
          .create();

      Business business = Instancio.of(Business.class)
          .set(field(Business::id), 1)
          .set(field(Business::name), "Test Business")
          .create();

      Report report = Instancio.of(Report.class)
          .set(field(Report::id), 1)
          .set(field(Report::user), user)
          .set(field(Report::business), business)
          .set(field(Report::day), reportDay)
          .set(field(Report::hours), hours)
          .create();

      List<Report> reports = List.of(report);

      // When
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result).hasSize(1);
      UserTimeReportSummary summary = result.get(0);
      assertThat(summary.user()).isEqualTo(user);
      assertThat(summary.start()).isEqualTo(from);
      assertThat(summary.end()).isEqualTo(to);
      assertThat(summary.totalHours()).isEqualTo(hours);
      assertThat(summary.totalOvertimeHours()).isEqualTo(BigDecimal.ZERO);
      assertThat(summary.totalAbsenceHours()).isEqualTo(BigDecimal.ZERO);
      assertThat(summary.dailyHours()).isNotNull();

      // Verify daily hours are filled for the entire range
      var dailyHoursMap = summary.dailyHours().hours();
      assertThat(dailyHoursMap).hasSize(7); // 7 days from 15th to 21st inclusive
      assertThat(dailyHoursMap.get(reportDay)).isEqualTo(hours);

      // Other days should have zero hours
      LocalDate current = from;
      while (!current.isAfter(to)) {
        if (!current.equals(reportDay)) {
          assertThat(dailyHoursMap.get(current)).isEqualTo(BigDecimal.ZERO);
        }
        current = current.plusDays(1);
      }
    }

    @Test
    @DisplayName("Should merge multiple reports for same user")
    void shouldMergeMultipleReportsForSameUser() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      LocalDate day1 = LocalDate.of(2024, 1, 16);
      LocalDate day2 = LocalDate.of(2024, 1, 17);
      BigDecimal hours1 = new BigDecimal("8.0");
      BigDecimal hours2 = new BigDecimal("6.5");

      User user = Instancio.of(User.class)
          .set(field(User::id), 1)
          .set(field(User::name), "John Doe")
          .create();

      Business business = Instancio.of(Business.class)
          .set(field(Business::id), 1)
          .create();

      Report report1 = Instancio.of(Report.class)
          .set(field(Report::user), user)
          .set(field(Report::business), business)
          .set(field(Report::day), day1)
          .set(field(Report::hours), hours1)
          .create();

      Report report2 = Instancio.of(Report.class)
          .set(field(Report::user), user)
          .set(field(Report::business), business)
          .set(field(Report::day), day2)
          .set(field(Report::hours), hours2)
          .create();

      List<Report> reports = List.of(report1, report2);

      // When
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result).hasSize(1);
      UserTimeReportSummary summary = result.get(0);
      assertThat(summary.user()).isEqualTo(user);
      assertThat(summary.totalHours()).isEqualTo(hours1.add(hours2));

      var dailyHoursMap = summary.dailyHours().hours();
      assertThat(dailyHoursMap.get(day1)).isEqualTo(hours1);
      assertThat(dailyHoursMap.get(day2)).isEqualTo(hours2);
    }

    @Test
    @DisplayName("Should handle multiple users with separate summaries")
    void shouldHandleMultipleUsersWithSeparateSummaries() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      LocalDate reportDay = LocalDate.of(2024, 1, 16);
      BigDecimal hours1 = new BigDecimal("8.0");
      BigDecimal hours2 = new BigDecimal("7.5");

      User user1 = Instancio.of(User.class)
          .set(field(User::id), 1)
          .set(field(User::name), "John Doe")
          .create();

      User user2 = Instancio.of(User.class)
          .set(field(User::id), 2)
          .set(field(User::name), "Jane Smith")
          .create();

      Business business = Instancio.of(Business.class)
          .set(field(Business::id), 1)
          .create();

      Report report1 = Instancio.of(Report.class)
          .set(field(Report::user), user1)
          .set(field(Report::business), business)
          .set(field(Report::day), reportDay)
          .set(field(Report::hours), hours1)
          .create();

      Report report2 = Instancio.of(Report.class)
          .set(field(Report::user), user2)
          .set(field(Report::business), business)
          .set(field(Report::day), reportDay)
          .set(field(Report::hours), hours2)
          .create();

      List<Report> reports = List.of(report1, report2);

      // When
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result).hasSize(2);

      // Find summaries by user ID for easier assertion
      UserTimeReportSummary summary1 = result.stream()
          .filter(s -> s.user().id().equals(1))
          .findFirst()
          .orElseThrow();

      UserTimeReportSummary summary2 = result.stream()
          .filter(s -> s.user().id().equals(2))
          .findFirst()
          .orElseThrow();

      assertThat(summary1.user()).isEqualTo(user1);
      assertThat(summary1.totalHours()).isEqualTo(hours1);
      assertThat(summary2.user()).isEqualTo(user2);
      assertThat(summary2.totalHours()).isEqualTo(hours2);
    }

    @Test
    @DisplayName("Should handle same user with multiple reports on same day")
    void shouldHandleSameUserWithMultipleReportsOnSameDay() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      LocalDate reportDay = LocalDate.of(2024, 1, 16);
      BigDecimal hours1 = new BigDecimal("4.0");
      BigDecimal hours2 = new BigDecimal("3.5");

      User user = Instancio.of(User.class)
          .set(field(User::id), 1)
          .set(field(User::name), "John Doe")
          .create();

      Business business1 = Instancio.of(Business.class)
          .set(field(Business::id), 1)
          .set(field(Business::name), "Business A")
          .create();

      Business business2 = Instancio.of(Business.class)
          .set(field(Business::id), 2)
          .set(field(Business::name), "Business B")
          .create();

      Report report1 = Instancio.of(Report.class)
          .set(field(Report::user), user)
          .set(field(Report::business), business1)
          .set(field(Report::day), reportDay)
          .set(field(Report::hours), hours1)
          .create();

      Report report2 = Instancio.of(Report.class)
          .set(field(Report::user), user)
          .set(field(Report::business), business2)
          .set(field(Report::day), reportDay)
          .set(field(Report::hours), hours2)
          .create();

      List<Report> reports = List.of(report1, report2);

      // When
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result).hasSize(1);
      UserTimeReportSummary summary = result.get(0);
      assertThat(summary.user()).isEqualTo(user);
      assertThat(summary.totalHours()).isEqualTo(hours1.add(hours2));

      var dailyHoursMap = summary.dailyHours().hours();
      assertThat(dailyHoursMap.get(reportDay)).isEqualTo(hours1.add(hours2));
    }

    @Test
    @DisplayName("Should handle edge case with zero hours")
    void shouldHandleEdgeCaseWithZeroHours() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      LocalDate reportDay = LocalDate.of(2024, 1, 16);
      BigDecimal zeroHours = BigDecimal.ZERO;

      User user = Instancio.of(User.class)
          .set(field(User::id), 1)
          .create();

      Business business = Instancio.of(Business.class)
          .set(field(Business::id), 1)
          .create();

      Report report = Instancio.of(Report.class)
          .set(field(Report::user), user)
          .set(field(Report::business), business)
          .set(field(Report::day), reportDay)
          .set(field(Report::hours), zeroHours)
          .create();

      List<Report> reports = List.of(report);

      // When
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result).hasSize(1);
      UserTimeReportSummary summary = result.get(0);
      assertThat(summary.totalHours()).isEqualTo(BigDecimal.ZERO);
      assertThat(summary.dailyHours().hours().get(reportDay)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle large dataset efficiently")
    void shouldHandleLargeDatasetEfficiently() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 1);
      LocalDate to = LocalDate.of(2024, 1, 31);

      // Create users first
      List<User> users = Instancio.ofList(User.class)
          .size(100)
          .generate(field(User::id), gen -> gen.ints().range(1, 101))
          .create();

      // Create 100 users with 31 days of reports each (3,100 reports total)
      List<Report> reports = Instancio.ofList(Report.class)
          .size(3100)
          .generate(field(Report::user), gen -> gen.oneOf(users))
          .generate(field(Report::day), gen -> gen.temporal().localDate().range(from, to))
          .generate(field(Report::hours), gen -> gen.math().bigDecimal().range(
              new BigDecimal("0.5"), new BigDecimal("12.0")).scale(1))
          .create();

      // When
      long startTime = System.currentTimeMillis();
      List<UserTimeReportSummary> result = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);
      long endTime = System.currentTimeMillis();

      // Then
      assertThat(result).hasSizeLessThanOrEqualTo(100); // Should have at most 100 users
      assertThat(endTime - startTime).isLessThan(1000); // Should complete within 1 second

      // Verify all summaries have correct date range
      result.forEach(summary -> {
        assertThat(summary.start()).isEqualTo(from);
        assertThat(summary.end()).isEqualTo(to);
        assertThat(summary.totalHours()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(summary.dailyHours().hours()).hasSize(31); // January has 31 days
      });
    }

    @Test
    @DisplayName("Should preserve user order consistency")
    void shouldPreserveUserOrderConsistency() {
      // Given
      LocalDate from = LocalDate.of(2024, 1, 15);
      LocalDate to = LocalDate.of(2024, 1, 21);
      LocalDate reportDay = LocalDate.of(2024, 1, 16);

      // Create users with unique sequential IDs
      List<User> users = List.of(
          Instancio.of(User.class).set(field(User::id), 1).create(),
          Instancio.of(User.class).set(field(User::id), 2).create(),
          Instancio.of(User.class).set(field(User::id), 3).create(),
          Instancio.of(User.class).set(field(User::id), 4).create(),
          Instancio.of(User.class).set(field(User::id), 5).create());

      Business business = Instancio.of(Business.class)
          .set(field(Business::id), 1)
          .create();

      List<Report> reports = users.stream()
          .map(user -> Instancio.of(Report.class)
              .set(field(Report::user), user)
              .set(field(Report::business), business)
              .set(field(Report::day), reportDay)
              .set(field(Report::hours), new BigDecimal("8.0"))
              .create())
          .toList();

      // When - Run multiple times to check consistency
      List<UserTimeReportSummary> result1 = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);
      List<UserTimeReportSummary> result2 = timesheetReportGeneratorService
          .computeWeeklyHoursReport(from, to, reports);

      // Then
      assertThat(result1).hasSize(5);
      assertThat(result2).hasSize(5);

      // Results should be deterministic (same order)
      for (int i = 0; i < result1.size(); i++) {
        assertThat(result1.get(i).user().id()).isEqualTo(result2.get(i).user().id());
      }
    }
  }
}
