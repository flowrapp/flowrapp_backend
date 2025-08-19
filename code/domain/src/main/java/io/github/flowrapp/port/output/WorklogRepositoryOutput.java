package io.github.flowrapp.port.output;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.value.WorklogFilteredRequest;

public interface WorklogRepositoryOutput {

  Optional<Worklog> findById(Integer worklogId);

  List<Worklog> findAllFiltered(WorklogFilteredRequest worklogFilteredRequest);

  Worklog save(Worklog worklog);

}
