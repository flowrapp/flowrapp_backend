package io.github.flowrapp.value;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogClockOutRequest(
    Integer worklogId,
    Integer businessId,
    Instant clockOut) {
}
