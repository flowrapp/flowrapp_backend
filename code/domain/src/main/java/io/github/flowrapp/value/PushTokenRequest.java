package io.github.flowrapp.value;

import io.github.flowrapp.model.Platform;

import lombok.Builder;

@Builder(toBuilder = true)
public record PushTokenRequest(
    String token,
    Platform platform,
    String deviceId) {
}
