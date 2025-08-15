package io.github.flowrapp.model;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record Business(
    Integer id,
    String name,
    User owner,
    Location location,
    OffsetDateTime createdAt) {
}
