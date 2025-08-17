package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import io.github.flowrapp.infrastructure.apirest.users.model.Login200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.LoginRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RefreshTokenRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.AuthDTOMapper;
import io.github.flowrapp.model.value.TokensResponse;
import io.github.flowrapp.port.input.UserAuthenticationUseCase;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class AuthenticationControllerTest {

  @Mock
  private UserAuthenticationUseCase userAuthenticationUseCase;

  @Spy
  private AuthDTOMapper authDTOMapper = Mappers.getMapper(AuthDTOMapper.class);

  @InjectMocks
  private AuthenticationController authenticationController;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void login_returnsOk(LoginRequestDTO loginRequestDTO, TokensResponse tokensResponse) {
    // GIVEN
    when(userAuthenticationUseCase.loginUser(argThat(argument -> argument.username().equals(loginRequestDTO.getUsername())
        && argument.password().equals(loginRequestDTO.getPassword()))))
            .thenReturn(tokensResponse);

    // WHEN
    var response = authenticationController.login(loginRequestDTO);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(tokensResponse.accessToken(), Login200ResponseDTO::getAccessToken)
        .returns(tokensResponse.refreshToken(), Login200ResponseDTO::getRefreshToken);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void refreshToken_returnsOk(RefreshTokenRequestDTO refreshTokenRequestDTO, TokensResponse tokensResponse) {
    // GIVEN
    when(userAuthenticationUseCase
        .refreshTokens(argThat(argument -> argument.refreshToken().equals(refreshTokenRequestDTO.getRefreshToken()))))
            .thenReturn(tokensResponse);

    // WHEN
    var response = authenticationController.refreshToken(refreshTokenRequestDTO);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(tokensResponse.accessToken(), Login200ResponseDTO::getAccessToken)
        .returns(tokensResponse.refreshToken(), Login200ResponseDTO::getRefreshToken);
  }
}
