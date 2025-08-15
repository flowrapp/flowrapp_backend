package io.github.flowrapp.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.With;

/** Represents a user in the system. */
@Builder(toBuilder = true)
@With
public record User(
    Integer id,
    String name,
    String mail,
    String phone,
    String passwordHash,
    boolean enabled,
    OffsetDateTime createdAt,
    List<Business> ownerBusinesses) {
}
