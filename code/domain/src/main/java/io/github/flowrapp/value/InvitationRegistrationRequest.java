package io.github.flowrapp.value;

import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record InvitationRegistrationRequest(
    UUID token,
    String username,
    String phone,
    String password) {
}
