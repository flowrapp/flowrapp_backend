package io.github.flowrapp.value;

/**
 * Interface for OAuth2 user information from different providers
 */
public interface OAuth2UserInfo {

  /**
   * Get the unique identifier for the user from the OAuth2 provider
   */
  String getId();

  /**
   * Get the user's email address
   */
  String getEmail();

  /**
   * Get the user's display name
   */
  String getName();

  /**
   * Get the user's avatar URL
   */
  String getAvatarUrl();

  /**
   * Get the OAuth2 provider name (e.g., "github", "google")
   */
  Provider getProvider();

  enum Provider {
    GITHUB,
    GOOGLE
  }

}
