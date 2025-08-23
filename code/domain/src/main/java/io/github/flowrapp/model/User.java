package io.github.flowrapp.model;

import java.time.Instant;

import io.github.flowrapp.value.OAuth2UserInfo;
import io.github.flowrapp.value.SensitiveInfo;
import io.github.flowrapp.value.UserCreationRequest;

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
    SensitiveInfo<String> passwordHash,
    UserRole role,
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
        .passwordHash(
            SensitiveInfo.of("")) // Password hash is not set
        .enabled(false) // Not enabled by default
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .build();
  }

  public static @NonNull User fromOauth2Info(@NonNull OAuth2UserInfo oAuth2UserInfo) {
    return User.builder()
        .name(oAuth2UserInfo.getName())
        .mail(oAuth2UserInfo.getEmail())
        .phone("")
        .passwordHash(SensitiveInfo.empty()) // No password for OAuth users
        .enabled(true) // OAuth users are enabled by default
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .build();
  }
}
