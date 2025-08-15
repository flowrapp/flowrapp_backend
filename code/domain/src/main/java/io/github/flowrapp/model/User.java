package io.github.flowrapp.model;

import java.time.OffsetDateTime;

import lombok.Builder;

/** Represents a user in the system. */
@Builder(toBuilder = true)
public record User(
    Integer id,
    String name,
    String mail,
    String phone,
    String passwordHash,
    boolean enabled,
    OffsetDateTime createdAt) {
}
