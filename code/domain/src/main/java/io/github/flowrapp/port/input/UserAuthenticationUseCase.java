package io.github.flowrapp.port.input;

import java.util.Optional;

import io.github.flowrapp.model.User;
import io.github.flowrapp.value.LoginRequest;
import io.github.flowrapp.value.OAuth2UserInfo;
import io.github.flowrapp.value.RefreshRequest;
import io.github.flowrapp.value.TokensResponse;

import org.jspecify.annotations.NonNull;

public interface UserAuthenticationUseCase {

  Optional<User> retrieveUserByMail(@NonNull String mail);

  Optional<User> updateUserPasswordHash(String mail, @NonNull String password);

  @NonNull
  TokensResponse loginUser(@NonNull LoginRequest request);

  @NonNull
  TokensResponse refreshTokens(@NonNull RefreshRequest request);

  @NonNull
  TokensResponse loginOauth2User(@NonNull String code, OAuth2UserInfo.Provider provider);

}
