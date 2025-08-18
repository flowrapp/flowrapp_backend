package io.github.flowrapp.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.value.UserTimeReportSummary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to generate timesheet reports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimesheetReportGeneratorService {

  /**
   * Computes a weekly hours report from a list of reports.
   */
  public List<UserTimeReportSummary> computeWeeklyHoursReport(LocalDate from, LocalDate to, List<Report> reports) {
    log.debug("Computing weekly hours report for {} reports", reports.size());

    // Group reports by user, then compute the total hours for each user
    return reports.stream()
        .collect(
            groupingBy(report -> report.user().id(),
                mapping(UserTimeReportSummary::fromReport,
                    reducing(UserTimeReportSummary::merge))))
        .values().stream()
        .filter(Optional::isPresent) // Should not be empty, but just in case
        .map(Optional::get)
        .map(timeReportSummary -> timeReportSummary.toBuilder()
            .start(from) // Update start/end to match the requested range
            .end(to)
            .build()
            .fillDailyHours()) // Fill daily hours for each summary
        .toList();
  }

}
