package io.github.flowrapp.model.value;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogClockInRequest(
    Integer businessId,
    Instant clockIn) {
}
