package io.github.flowrapp.infrastructure.input.rest.mainapi.security.value;

import java.util.Map;

import io.github.flowrapp.value.OAuth2UserInfo;

import lombok.Builder;

/**
 * Implementation of OAuth2UserInfo for GitHub
 */
@Builder(toBuilder = true)
public record GitHubUserInfo(
    String id,
    String email,
    String name,
    String avatarUrl) implements OAuth2UserInfo {

  public static final String PROVIDER = "github";

  /**
   * Factory method to create GitHubUserInfo from GitHub API response
   *
   * @param attributes Map of attributes from GitHub API
   * @param email Email from GitHub API (might be retrieved separately due to privacy settings)
   * @return GitHubUserInfo instance
   */
  public static GitHubUserInfo fromAttributes(Map<String, Object> attributes, String email) {
    String id = attributes.get("id").toString();
    String name = (String) attributes.getOrDefault("name", "");
    if (name == null || name.isBlank()) {
      name = (String) attributes.getOrDefault("login", "GitHub User");
    }
    String avatarUrl = (String) attributes.getOrDefault("avatar_url", "");

    return new GitHubUserInfo(id, email, name, avatarUrl);
  }

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
    return Provider.GITHUB;
  }
}
