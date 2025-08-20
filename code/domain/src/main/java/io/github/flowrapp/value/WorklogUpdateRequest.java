package io.github.flowrapp.value;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogUpdateRequest(
    Integer worklogId,
    OffsetDateTime clockIn,
    OffsetDateTime clockOut) {
}
