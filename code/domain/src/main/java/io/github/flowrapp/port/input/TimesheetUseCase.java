package io.github.flowrapp.port.input;

import java.util.List;

import io.github.flowrapp.model.value.TimesheetFilterRequest;
import io.github.flowrapp.model.value.UserTimeReportSummary;

public interface TimesheetUseCase {

  List<UserTimeReportSummary> getSummaryReport(TimesheetFilterRequest timesheetFilterRequest);

  List<UserTimeReportSummary> getUserSummaryReport(TimesheetFilterRequest timesheetFilterRequest);

}
