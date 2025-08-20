package io.github.flowrapp.value;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogClockOutRequest(
    Integer worklogId,
    Integer businessId,
    OffsetDateTime clockOut) {
}
