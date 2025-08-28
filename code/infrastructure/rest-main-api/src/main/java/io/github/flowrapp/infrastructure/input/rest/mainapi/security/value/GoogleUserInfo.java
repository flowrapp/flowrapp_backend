package io.github.flowrapp.infrastructure.input.rest.mainapi.security.value;

import io.github.flowrapp.value.OAuth2UserInfo;

import lombok.Builder;

/**
 * Record implementation for Google OAuth2 user information. Maps Google OAuth2 user attributes to the common OAuth2UserInfo interface.
 */
@Builder(toBuilder = true)
public record GoogleUserInfo(
    String id,
    String email,
    String name,
    String avatarUrl,
    String provider) implements OAuth2UserInfo {

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAvatarUrl() {
    return avatarUrl;
  }

  @Override
  public Provider getProvider() {
    return Provider.GOOGLE;
  }

}
