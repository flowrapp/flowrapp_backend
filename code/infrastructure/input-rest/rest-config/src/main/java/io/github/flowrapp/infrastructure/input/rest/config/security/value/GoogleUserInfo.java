package io.github.flowrapp.infrastructure.input.rest.config.security.value;

import java.util.Map;

import io.github.flowrapp.value.OAuth2UserInfo;

/**
 * Record implementation for Google OAuth2 user information. Maps Google OAuth2 user attributes to the common OAuth2UserInfo interface.
 */
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

  /**
   * Creates a GoogleUserInfo instance from Google OAuth2 attributes.
   *
   * @param attributes the OAuth2 attributes from Google
   * @return GoogleUserInfo instance
   */
  public static GoogleUserInfo fromAttributes(Map<String, Object> attributes) {
    String id = (String) attributes.get("sub");
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String picture = (String) attributes.get("picture");

    // Fallback to given_name + family_name if name is not available
    if (name == null || name.trim().isEmpty()) {
      String givenName = (String) attributes.get("given_name");
      String familyName = (String) attributes.get("family_name");
      if (givenName != null && familyName != null) {
        name = givenName + " " + familyName;
      } else if (givenName != null) {
        name = givenName;
      } else {
        name = "Google User";
      }
    }

    return new GoogleUserInfo(
        id,
        email,
        name,
        picture,
        "google");
  }
}
