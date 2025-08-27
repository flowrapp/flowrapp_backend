package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.github.flowrapp.infrastructure.apirest.users.api.PushTokensApi;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterPushTokenRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.PushTokenDTOMapper;
import io.github.flowrapp.port.input.PushTokenUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PushTokensController implements PushTokensApi {

  private final PushTokenUseCase pushTokenUseCase;

  private final PushTokenDTOMapper pushTokenDTOMapper;

  @Override
  public ResponseEntity<Void> registerPushToken(RegisterPushTokenRequestDTO registerPushTokenRequestDTO) {
    pushTokenUseCase.create(
        pushTokenDTOMapper.rest2domain(registerPushTokenRequestDTO));

    return ResponseEntity.status(CREATED).build();
  }

  @Override
  public ResponseEntity<Void> deletePushToken(String deviceId) {
    pushTokenUseCase.delete(deviceId);

    return ResponseEntity.ok().build();
  }

}
