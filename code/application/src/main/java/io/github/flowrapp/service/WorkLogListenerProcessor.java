package io.github.flowrapp.service;

import java.util.Optional;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.output.ReportRepositoryOutput;
import io.github.flowrapp.value.CreateWorklogEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Service that listens to work log events and processes them by updating or creating reports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkLogListenerProcessor {

  private final ReportRepositoryOutput reportRepositoryOutput;

  @Async("virtualThreadsExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void listenToWorkLogEvents(CreateWorklogEvent event) {
    log.info("Received work log event: {}", event);

    val eventWorkLog = event.getWorklog();
    if (!eventWorkLog.isValid()) {
      log.warn("Invalid work log event: {}", eventWorkLog);
      return; // Ignore invalid work logs
    }

    switch (event.getEventType()) {
      case CREATE -> processCreation(eventWorkLog);
      case UPDATE -> processUpdate(eventWorkLog, event.getPrevious());
      case DELETE -> processDelete(eventWorkLog);
    }
  }

  private void processCreation(Worklog eventWorkLog) {
    log.info("Processing creation of worklog: {}", eventWorkLog);

    val newWorklog = findReportByDay(eventWorkLog)
        .map(report -> report.sum(eventWorkLog.getSeconds())) // If exists, sum the seconds
        .orElseGet(() -> Report.fromWorklog(eventWorkLog)); // If not exists, create a new report from the worklog

    reportRepositoryOutput.save(newWorklog);
    log.debug("Processed work log event: {} for new worklog {}", eventWorkLog, newWorklog);
  }

  private void processUpdate(Worklog eventWorkLog, Worklog previous) {
    log.debug("Processing update of worklog: {}", eventWorkLog);

    val reportOpt = findReportByDay(eventWorkLog);
    if (reportOpt.isEmpty()) {
      log.warn("No report found while updating for worklog??: {}", eventWorkLog);
      return;
    }

    val savedReport = reportRepositoryOutput.save(
        reportOpt.get()
            .minus(previous.getSeconds()) // Subtract previous seconds
            .sum(eventWorkLog.getSeconds())); // Add new seconds

    log.debug("Saved report for update worklog: {}", savedReport);
  }

  private void processDelete(Worklog eventWorkLog) {
    log.debug("Processing deletion of worklog: {}", eventWorkLog);

    val reportOpt = findReportByDay(eventWorkLog);
    if (reportOpt.isEmpty()) {
      log.warn("No report found while deleting for worklog??: {}", eventWorkLog);
      return;
    }

    val savedReport = reportRepositoryOutput.save(
        reportOpt.get()
            .minus(eventWorkLog.getSeconds())); // Subtract seconds from the report

    log.debug("Saved report for deletion worklog: {}", savedReport);
  }

  private Optional<Report> findReportByDay(Worklog worklog) {
    return reportRepositoryOutput.getByDay(worklog.user().id(), worklog.business().id(), worklog.getDay());
  }

}
