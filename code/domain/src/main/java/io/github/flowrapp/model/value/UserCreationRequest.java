package io.github.flowrapp.model.value;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserCreationRequest(
    String username,
    String mail,
    BusinessCreationRequest business) {
}
