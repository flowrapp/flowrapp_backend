package io.github.flowrapp.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

@Builder(toBuilder = true)
@With
public record Invitation(
    Integer id,
    User invited,
    User invitedBy,
    Business business,
    UUID token,
    BusinessUserRole role,
    Instant createdAt,
    Instant expiresAt,
    InvitationStatus status) {

  public boolean hasExpired() {
    return expiresAt.isBefore(Instant.now());
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
        .expiresAt(Instant.now()) // Set expiresAt to now when accepted
        .build();
  }

  /** Creates a new invitation for a user to join a business. */
  public static Invitation create(User invited, Business business, User invitedBy, BusinessUserRole role) {
    return Invitation.builder()
        .invited(invited)
        .business(business)
        .invitedBy(invitedBy)
        .token(UUID.randomUUID())
        .role(role)
        .createdAt(Instant.now())
        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS)) // TODO: make configurable
        .status(InvitationStatus.PENDING)
        .build();
  }

}
