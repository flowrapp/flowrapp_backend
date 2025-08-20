package io.github.flowrapp.port.input;

import java.util.List;

import io.github.flowrapp.value.TimesheetFilterRequest;
import io.github.flowrapp.value.UserTimeReportSummary;

public interface TimesheetUseCase {

  List<UserTimeReportSummary> getSummaryReport(TimesheetFilterRequest timesheetFilterRequest);

  List<UserTimeReportSummary> getUserSummaryReport(TimesheetFilterRequest timesheetFilterRequest);

}
