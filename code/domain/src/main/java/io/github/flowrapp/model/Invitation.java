package io.github.flowrapp.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record Invitation(
    User invited,
    User invitedBy,
    Business business,
    UUID token,
    UserRole role,
    OffsetDateTime createdAt,
    OffsetDateTime expiresAt,
    InvitationStatus status) {

  public boolean isExpired() {
    return expiresAt != null && expiresAt.isBefore(OffsetDateTime.now());
  }

  /** Creates a new invitation for a user to join a business. */
  public static Invitation createInvitation(User invited, Business business, User invitedBy, UserRole role) {
    return Invitation.builder()
        .invited(invited)
        .business(business)
        .invitedBy(invitedBy)
        .token(UUID.randomUUID())
        .role(role)
        .createdAt(OffsetDateTime.now())
        .expiresAt(OffsetDateTime.now().plusDays(7)) // TODO: make configurable
        .status(InvitationStatus.PENDING)
        .build();
  }

}
