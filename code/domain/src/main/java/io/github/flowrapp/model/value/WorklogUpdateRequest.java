package io.github.flowrapp.model.value;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogUpdateRequest(
    Integer worklogId,
    Instant clockIn,
    Instant clockOut) {
}
