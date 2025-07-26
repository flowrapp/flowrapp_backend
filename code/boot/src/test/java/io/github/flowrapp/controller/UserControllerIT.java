package com.inditex.flowrapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.post;

import com.inditex.flowrapp.DatabaseData;
import com.inditex.flowrapp.config.InitDatabase;

import io.github.flowrapp.Application;
import io.github.flowrapp.infrastructure.input.rest.users.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.users.dto.UserResponseDTO;
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
@InitDatabase
class UserControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void testGetUser_returnsUser_whenExists() {
    // GIVEN
    val user = new UserRequestDTO(DatabaseData.USER_USERNAME);

    // WHEN
    val response = testRestTemplate.exchange(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(user), UserResponseDTO.class);

    assertThat(response)
        .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .returns(DatabaseData.USER_USERNAME, UserResponseDTO::name)
        .returns(DatabaseData.USER_DNI, UserResponseDTO::dni);
  }

}
