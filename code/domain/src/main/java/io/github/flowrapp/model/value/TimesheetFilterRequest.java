package io.github.flowrapp.model.value;

import java.time.LocalDate;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record TimesheetFilterRequest(
    Integer businessId,
    Integer userId,
    LocalDate from,
    LocalDate to) {

}
