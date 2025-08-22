package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import io.github.flowrapp.infrastructure.apirest.users.model.Login200ResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.dto.GitHubOAuthRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.dto.GoogleOAuthRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.AuthDTOMapper;
import io.github.flowrapp.port.input.UserAuthenticationUseCase;
import io.github.flowrapp.value.OAuth2UserInfo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for OAuth2 authentication endpoints. Handles GitHub and Google OAuth2 token validation and user authentication.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth/oauth")
@RequiredArgsConstructor
public class OAuth2AuthController {

  private final UserAuthenticationUseCase userAuthenticationUseCase;

  private final AuthDTOMapper authDTOMapper;

  /**
   * Authenticates a user using GitHub OAuth2 access token.
   *
   * @param request GitHub OAuth2 request containing access token
   * @return JWT tokens response or unauthorized if token is invalid
   */
  @PostMapping("/github")
  public ResponseEntity<Login200ResponseDTO> authenticateGitHub(@Valid @RequestBody GitHubOAuthRequestDTO request) {
    log.debug("GitHub OAuth2 authentication request received");

    return ResponseEntity.ok(
        authDTOMapper.domain2rest(
            userAuthenticationUseCase.loginOauth2User(request.accessToken(), OAuth2UserInfo.Provider.GITHUB)));
  }

  /**
   * Authenticates a user using Google OAuth2 ID token.
   *
   * @param request Google OAuth2 request containing ID token
   * @return JWT tokens response or unauthorized if token is invalid
   */
  @PostMapping("/google")
  public ResponseEntity<Login200ResponseDTO> authenticateGoogle(@Valid @RequestBody GoogleOAuthRequestDTO request) {
    log.debug("Google OAuth2 authentication request received");

    return ResponseEntity.ok(
        authDTOMapper.domain2rest(
            userAuthenticationUseCase.loginOauth2User(request.idToken(), OAuth2UserInfo.Provider.GOOGLE)));
  }

}
