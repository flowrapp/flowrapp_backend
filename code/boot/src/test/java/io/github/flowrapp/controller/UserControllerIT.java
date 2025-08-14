package io.github.flowrapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.post;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUser200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserRequestDTO;

import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
@InitDatabase
@WithUserDetails(DatabaseData.MOCK_USER_USERNAME)
@Disabled
class UserControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void testGetUser_returnsUser_whenExists() {
    // GIVEN
    val user = new GetUserRequestDTO(DatabaseData.MOCK_USER_USERNAME);

    // WHEN
    val response = testRestTemplate.exchange(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(user), GetUser200ResponseDTO.class);

    assertThat(response)
        .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .returns(DatabaseData.MOCK_USER_USERNAME, GetUser200ResponseDTO::getName)
        .returns(DatabaseData.MOCK_USER_DNI, GetUser200ResponseDTO::getDni);
  }

}
