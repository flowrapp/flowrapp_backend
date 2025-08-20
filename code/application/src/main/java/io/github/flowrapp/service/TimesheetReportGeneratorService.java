package io.github.flowrapp.service;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.List;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.value.UserTimeReportSummary;

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

    // Aggregate reports by user in a single pass without intermediate Optionals
    return reports.stream()
        .collect(toMap(r -> r.user().id(),
            UserTimeReportSummary::fromReport,
            UserTimeReportSummary::merge))
        .values().stream()
        .map(timeReportSummary -> timeReportSummary.toBuilder()
            .start(from) // Update start/end to match the requested range
            .end(to)
            .build()
            .fillDailyHours()) // Fill daily hours for each summary
        .toList();
  }

}
