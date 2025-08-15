package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import io.github.flowrapp.infrastructure.apirest.users.api.AuthenticationApi;
import io.github.flowrapp.infrastructure.apirest.users.model.Login200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.LoginRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RefreshTokenRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.AuthDTOMapper;
import io.github.flowrapp.port.input.UserAuthenticationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApi {

  private final UserAuthenticationUseCase userAuthenticationUseCase;

  private final AuthDTOMapper authDTOMapper;

  @Override
  public ResponseEntity<Login200ResponseDTO> login(LoginRequestDTO loginRequestDTO) {
    val response = userAuthenticationUseCase.loginUser(
        authDTOMapper.rest2domain(loginRequestDTO));

    return ResponseEntity.ok(
        authDTOMapper.domain2rest(response));
  }

  @Override
  public ResponseEntity<Login200ResponseDTO> refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
    val response = userAuthenticationUseCase.refreshTokens(
        authDTOMapper.rest2domain(refreshTokenRequestDTO));

    return ResponseEntity.ok(
        authDTOMapper.domain2rest(response));
  }

}
