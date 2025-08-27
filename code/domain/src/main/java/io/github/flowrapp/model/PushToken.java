package io.github.flowrapp.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.github.flowrapp.value.PushTokenRequest;

import lombok.Builder;

@Builder(toBuilder = true)
public record PushToken(
    Integer id,
    User user,
    UUID token,
    String deviceId,
    Platform platform,
    OffsetDateTime createdAt) {

  public static PushToken fromRequest(PushTokenRequest request, User user) {
    return PushToken.builder()
        .user(user)
        .token(
            UUID.fromString(request.token()))
        .deviceId(request.deviceId())
        .platform(request.platform())
        .createdAt(OffsetDateTime.now())
        .build();
  }

}
