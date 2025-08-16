package io.github.flowrapp.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;
import lombok.NonNull;

@Builder(toBuilder = true)
public record Invitation(
    Integer id,
    User invited,
    User invitedBy,
    Business business,
    UUID token,
    UserRole role,
    OffsetDateTime createdAt,
    OffsetDateTime expiresAt,
    InvitationStatus status) {

  public boolean hasExpired() {
    return expiresAt != null && expiresAt.isBefore(OffsetDateTime.now());
  }

  public boolean isPending() {
    return status == InvitationStatus.PENDING;
  }

  public boolean isInvited(@NonNull User user) {
    return Objects.equals(invited.id(), user.id());
  }

  public Invitation accepted() {
    return this.toBuilder()
        .status(InvitationStatus.ACCEPTED)
        .expiresAt(OffsetDateTime.now()) // Set expiresAt to now when accepted
        .build();
  }

  /** Creates a new invitation for a user to join a business. */
  public static Invitation create(User invited, Business business, User invitedBy, UserRole role) {
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
