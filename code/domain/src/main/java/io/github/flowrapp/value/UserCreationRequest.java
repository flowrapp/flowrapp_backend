package io.github.flowrapp.value;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserCreationRequest(
    String username,
    String mail,
    BusinessCreationRequest business) {
}
