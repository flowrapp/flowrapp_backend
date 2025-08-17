package io.github.flowrapp.model.value;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogClockOutRequest(
    Integer worklogId,
    Integer businessId,
    OffsetDateTime clockOut) {
}
