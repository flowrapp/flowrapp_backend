package io.github.flowrapp.model.value;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorklogUpdateRequest(
    Integer worklogId,
    OffsetDateTime clockIn,
    OffsetDateTime clockOut) {
}
