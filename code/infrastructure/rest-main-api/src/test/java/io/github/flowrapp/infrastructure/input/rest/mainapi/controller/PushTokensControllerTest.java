package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;

import io.github.flowrapp.infrastructure.apirest.users.model.RegisterPushTokenRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.PushTokenDTOMapper;
import io.github.flowrapp.port.input.PushTokenUseCase;
import io.github.flowrapp.value.PushTokenRequest;

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
class PushTokensControllerTest {

  @Mock
  private PushTokenUseCase pushTokenUseCase;

  @Spy
  private PushTokenDTOMapper pushTokenDTOMapper = Mappers.getMapper(PushTokenDTOMapper.class);

  @InjectMocks
  private PushTokensController controller;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void registerPushToken(RegisterPushTokenRequestDTO requestDTO) {
    // GIVEN

    // Act
    ResponseEntity<Void> response = controller.registerPushToken(requestDTO);

    // Assert
    verify(pushTokenDTOMapper).rest2domain(requestDTO);
    verify(pushTokenUseCase).create(assertArg(request -> assertThat(request)
        .isNotNull()
        .returns(requestDTO.getDeviceId(), PushTokenRequest::deviceId)
        .returns(requestDTO.getPlatform().toString(), request1 -> request1.platform().toString())
        .returns(requestDTO.getToken(), PushTokenRequest::token)));
    assertEquals(ResponseEntity.ok().build(), response);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deletePushToken(String deviceId) {
    // Arrange

    // Act
    ResponseEntity<Void> response = controller.deletePushToken(deviceId);

    // Assert
    verify(pushTokenUseCase).delete(deviceId);
    assertEquals(ResponseEntity.ok().build(), response);
  }
}
