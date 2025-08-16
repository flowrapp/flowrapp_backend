package io.github.flowrapp.model;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record BusinessUser(
    User user,
    Business business,
    UserRole role,
    User invitedBy,
    OffsetDateTime joinedAt) {
}
