package io.github.flowrapp.model.value;

import java.time.LocalDate;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record WorklogFilteredRequest(
    Integer userId,
    Integer businessId,
    LocalDate from,
    LocalDate to,
    LocalDate date) {
}
