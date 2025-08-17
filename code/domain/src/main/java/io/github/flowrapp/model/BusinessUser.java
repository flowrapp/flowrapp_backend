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

  public static BusinessUser fromInvitation(Invitation invitation) {
    return BusinessUser.builder()
        .user(invitation.invited())
        .business(invitation.business())
        .role(invitation.role())
        .invitedBy(invitation.invitedBy())
        .joinedAt(OffsetDateTime.now()) // Set joinedAt to now when creating from invitation
        .build();
  }

}
