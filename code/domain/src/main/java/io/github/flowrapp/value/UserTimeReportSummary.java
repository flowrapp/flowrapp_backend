package io.github.flowrapp.value;

import java.time.LocalDate;
import java.util.Objects;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.Seconds;
import io.github.flowrapp.model.User;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record UserTimeReportSummary(
    User user,
    LocalDate start,
    LocalDate end,
    Seconds totalSeconds,
    Seconds totalOvertimeSeconds,
    Seconds totalAbsenceSeconds,
    DayHoursPairList dailySeconds) {

  public UserTimeReportSummary fillDailyHours() {
    return this.withDailySeconds(
        dailySeconds.fill(start, end));
  }

  public UserTimeReportSummary merge(UserTimeReportSummary other) {
    if (!Objects.equals(other.user.id(), this.user.id())) {
      throw new IllegalArgumentException("Cannot merge summaries for different users");
    }

    return this.toBuilder()
        .start(this.start.isBefore(other.start) ? this.start : other.start)
        .end(this.end.isAfter(other.end) ? this.end : other.end)
        .totalSeconds(this.totalSeconds.add(other.totalSeconds))
        .totalOvertimeSeconds(this.totalOvertimeSeconds.add(other.totalOvertimeSeconds))
        .totalAbsenceSeconds(this.totalAbsenceSeconds.add(other.totalAbsenceSeconds))
        .dailySeconds(
            this.dailySeconds.merge(other.dailySeconds))
        .build();
  }

  public static UserTimeReportSummary fromReport(Report report) {
    return UserTimeReportSummary.builder()
        .user(report.user())
        .start(report.day())
        .end(report.day())
        .totalSeconds(report.seconds())
        .totalOvertimeSeconds(Seconds.ZERO)
        .totalAbsenceSeconds(Seconds.ZERO)
        .dailySeconds(DayHoursPairList.of(
            report.day(), report.seconds()))
        .build();
  }

}
