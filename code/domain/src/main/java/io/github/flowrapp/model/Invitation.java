package io.github.flowrapp.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record Invitation(
  long id,
  User invited,
  User invitedBy,
  Business business,
  UUID token,
  UserRole role,
  OffsetDateTime createdAt,
  OffsetDateTime expiresAt,
  InvitationStatus status
) {
}
