package io.github.flowrapp.port.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.github.flowrapp.model.Report;
import io.github.flowrapp.model.value.TimesheetFilterRequest;

import org.jspecify.annotations.NonNull;

public interface ReportRepositoryOutput {

  List<Report> findAll(@NonNull TimesheetFilterRequest timesheetFilterRequest);

  Optional<Report> getByDay(@NonNull Integer userId, @NonNull Integer businessId, @NonNull LocalDate day);

  Report save(Report report);

}
