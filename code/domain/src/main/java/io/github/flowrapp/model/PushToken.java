package io.github.flowrapp.model;

import java.time.OffsetDateTime;

import io.github.flowrapp.value.PushTokenRequest;

import lombok.Builder;

@Builder(toBuilder = true)
public record PushToken(
    Integer id,
    User user,
    String token,
    String deviceId,
    Platform platform,
    OffsetDateTime createdAt) {

  public static PushToken fromRequest(PushTokenRequest request, User user) {
    return PushToken.builder()
        .user(user)
        .token(request.token())
        .deviceId(request.deviceId())
        .platform(request.platform())
        .createdAt(OffsetDateTime.now())
        .build();
  }

}
