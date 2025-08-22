package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.value.OAuth2UserInfo;

public interface OAuthServiceOutput {

  /**
   * Retrieves user information from an OAuth2 token.
   *
   * @param token the OAuth2 token
   * @param provider the OAuth2 provider (e.g., GOOGLE, FACEBOOK)
   * @return an Optional containing OAuth2UserInfo if the token is valid, otherwise an empty Optional
   */
  Optional<OAuth2UserInfo> getUserFromToken(String token, OAuth2UserInfo.Provider provider);

}
