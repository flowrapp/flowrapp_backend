package io.github.flowrapp.model;

import java.time.Instant;

import io.github.flowrapp.model.value.UserCreationRequest;

import lombok.Builder;
import lombok.With;
import org.jspecify.annotations.NonNull;

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
    Instant createdAt) {

  /** Creates a new user from a user creation request. */
  public static User fromUserCreationRequest(UserCreationRequest userCreationRequest) {
    return of(userCreationRequest.username(), userCreationRequest.mail());
  }

  public static User fromMail(@NonNull String mail) {
    var username = mail.split("@")[0];
    return of(username, mail);
  }

  public static User of(String username, String mail) {
    return User.builder()
        .name(username)
        .mail(mail)
        .phone("") // Phone is not provided
        .passwordHash("") // Password hash is not set
        .enabled(false) // Not enabled by default
        .createdAt(Instant.now())
        .build();
  }

}
