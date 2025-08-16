package io.github.flowrapp.model;

import java.time.OffsetDateTime;

import io.github.flowrapp.model.value.UserCreationRequest;

import lombok.Builder;
import lombok.With;

/** Represents a user in the system. */
@Builder(toBuilder = true)
@With
public record User(
    Integer id,
    String name,
    String mail,
    String phone,
    String passwordHash,
    boolean enabled,
    OffsetDateTime createdAt) {

  /** Creates a new user from a user creation request. */
  public static User fromUserCreationRequest(UserCreationRequest userCreationRequest) {
    return User.builder()
        .name(userCreationRequest.username())
        .mail(userCreationRequest.mail())
        .phone("") // Phone is not provided in the request
        .passwordHash("")
        .enabled(false) // Not enabled until accepts invitation
        .createdAt(OffsetDateTime.now())
        .build();
  }

}
