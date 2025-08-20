package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PingControllerTest {

  @InjectMocks
  private PingController pingController;

  @Test
  void ping() {
    // GIVEN

    // WHEN
    val result = pingController.ping();

    // THEN
    assertThat(result)
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .satisfies(dto -> {
          assertNotNull(dto.getStatus());
          assertNotNull(dto.getTimestamp());
        });
  }

}
