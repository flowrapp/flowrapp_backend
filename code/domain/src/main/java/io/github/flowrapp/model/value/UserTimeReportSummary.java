package io.github.flowrapp.model.value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.User;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record UserTimeReportSummary(
    User user,
    LocalDate start,
    LocalDate end,
    BigDecimal totalHours,
    BigDecimal totalOvertimeHours,
    BigDecimal totalAbsenceHours,
    DayHoursPairList dailyHours) {

  public UserTimeReportSummary fillDailyHours() {
    return this.withDailyHours(
        dailyHours.fill(start, end));
  }

  public UserTimeReportSummary merge(UserTimeReportSummary other) {
    if (!Objects.equals(other.user.id(), this.user.id())) {
      throw new IllegalArgumentException("Cannot merge summaries for different users");
    }

    return this.toBuilder()
        .start(this.start.isBefore(other.start) ? this.start : other.start)
        .end(this.end.isAfter(other.end) ? this.end : other.end)
        .totalHours(this.totalHours.add(other.totalHours))
        .totalOvertimeHours(this.totalOvertimeHours.add(other.totalOvertimeHours))
        .totalAbsenceHours(this.totalAbsenceHours.add(other.totalAbsenceHours))
        .dailyHours(
            this.dailyHours.merge(other.dailyHours))
        .build();
  }

  public static UserTimeReportSummary fromReport(Report report) {
    return UserTimeReportSummary.builder()
        .user(report.user())
        .start(report.day())
        .end(report.day())
        .totalHours(report.hours())
        .totalOvertimeHours(BigDecimal.ZERO)
        .totalAbsenceHours(BigDecimal.ZERO)
        .dailyHours(DayHoursPairList.of(
            report.day(), report.hours()))
        .build();
  }

}
