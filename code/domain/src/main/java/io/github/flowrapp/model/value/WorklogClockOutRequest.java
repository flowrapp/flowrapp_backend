package io.github.flowrapp.model.value;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogClockOutRequest(
    Integer worklogId,
    Integer businessId,
    Instant clockOut) {
}
