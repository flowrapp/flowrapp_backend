package io.github.flowrapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;

import io.github.flowrapp.Application;
import io.github.flowrapp.infrastructure.apirest.users.model.Ping200ResponseDTO;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
class PingControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void testGetUser_returnsUser_whenExists() {
    // GIVEN

    // WHEN
    val response = testRestTemplate.exchange(get("/api/v1/ping")
        .accept(MediaType.APPLICATION_JSON)
        .build(), Ping200ResponseDTO.class);

    // THEN
    assertThat(response)
        .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .satisfies(dto -> {
          assertThat(dto).isNotNull();
          assertThat(dto.getStatus()).isNotBlank();
          assertThat(dto.getTimestamp()).isNotNull();
        });
  }

}
