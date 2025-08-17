package io.github.flowrapp.model.value;

import io.github.flowrapp.model.UserRole;

import lombok.Builder;

@Builder(toBuilder = true)
public record InvitationCreationRequest(
    Integer businessId,
    String email,
    UserRole role) {
}
