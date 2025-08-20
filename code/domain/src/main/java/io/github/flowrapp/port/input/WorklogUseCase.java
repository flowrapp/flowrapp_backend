package io.github.flowrapp.port.input;

import java.util.List;

import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.value.WorklogClockInRequest;
import io.github.flowrapp.value.WorklogClockOutRequest;
import io.github.flowrapp.value.WorklogFilteredRequest;
import io.github.flowrapp.value.WorklogUpdateRequest;

public interface WorklogUseCase {

  Worklog clockIn(WorklogClockInRequest worklogClockInRequest);

  Worklog clockOut(WorklogClockOutRequest worklogClockOutRequest);

  Worklog updateWorklog(WorklogUpdateRequest worklogUpdateRequest);

  Worklog getById(Integer worklogId);

  List<Worklog> getUserWorklogs(WorklogFilteredRequest worklogFilteredRequest);

  List<Worklog> getBusinessWorklogs(WorklogFilteredRequest worklogFilteredRequest);

}
