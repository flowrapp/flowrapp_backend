package io.github.flowrapp.model;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record BusinessUser(
    User user,
    Business business,
    BusinessUserRole role,
    User invitedBy,
    Instant joinedAt) {

  public static BusinessUser fromInvitation(Invitation invitation) {
    return BusinessUser.builder()
        .user(invitation.invited())
        .business(invitation.business())
        .role(invitation.role())
        .invitedBy(invitation.invitedBy())
        .joinedAt(Instant.now()) // Set joinedAt to now when creating from invitation
        .build();
  }

}
