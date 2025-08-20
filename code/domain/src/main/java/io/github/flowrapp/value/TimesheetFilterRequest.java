package io.github.flowrapp.value;

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
