package io.github.flowrapp.value;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogClockInRequest(
    Integer businessId,
    Instant clockIn) {
}
