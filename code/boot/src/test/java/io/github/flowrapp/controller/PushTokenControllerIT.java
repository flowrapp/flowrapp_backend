package io.github.flowrapp.controller;

import static io.github.flowrapp.DatabaseData.ADMIN_ID;
import static io.github.flowrapp.DatabaseData.ADMIN_MAIL;
import static io.github.flowrapp.TestUtils.basicAuth;
import static io.github.flowrapp.controller.InvitationControllerIT.ADMIN_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.springframework.http.RequestEntity.delete;
import static org.springframework.http.RequestEntity.post;

import java.util.UUID;

import io.github.flowrapp.Application;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterPushTokenRequestDTO;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.PushTokenJpaRepository;
import io.github.flowrapp.port.output.MailSenderPort;
import io.github.flowrapp.port.output.PushTokenOutput;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
@Sql(scripts = "/scripts/populate_push_tokens.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@InitDatabase
public class PushTokenControllerIT {

  private static final String API_BASE_URL = "/api/v1/push-tokens";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private PushTokenJpaRepository pushTokenJpaRepository;

  @MockitoSpyBean
  private PushTokenOutput pushTokenOutput; // spy bean to verify interactions if needed

  @MockitoBean
  private MailSenderPort mailSender; // mock mail sender to avoid sending real emails during tests

  @Test
  @Sql("classpath:scripts/populate_push_tokens.sql")
  void testRegisterPushToken() {
    // Given
    var request = new RegisterPushTokenRequestDTO()
        .deviceId("device789")
        .platform(RegisterPushTokenRequestDTO.PlatformEnum.ANDROID)
        .token(UUID.randomUUID().toString());

    // When
    var response = testRestTemplate.exchange(post(API_BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuth(ADMIN_MAIL, ADMIN_PASSWORD))
        .body(request), Void.class);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(pushTokenJpaRepository.findByUserIdAndDeviceId(ADMIN_ID, "device789").isPresent());
  }

  @Test
  @Sql("classpath:scripts/populate_push_tokens.sql")
  void testRegisterPushToken_whenThereIsAlreadyOneForThatDevice() {
    // Given
    var request = new RegisterPushTokenRequestDTO()
        .deviceId("device123")
        .platform(RegisterPushTokenRequestDTO.PlatformEnum.ANDROID)
        .token(UUID.randomUUID().toString());

    // When
    var response = testRestTemplate.exchange(post(API_BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuth(ADMIN_MAIL, ADMIN_PASSWORD))
        .body(request), Void.class);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(pushTokenOutput).deleteById(anyInt()); // verify that the old token was deleted
    assertTrue(pushTokenJpaRepository.findByUserIdAndDeviceId(ADMIN_ID, "device123").isPresent());
  }

  @Test
  void testDeletePushToken() {
    // Given

    // When
    var response = testRestTemplate.exchange(delete(API_BASE_URL + "/" + "device123")
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuth(ADMIN_MAIL, ADMIN_PASSWORD))
        .build(), Void.class);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(pushTokenJpaRepository.findByUserIdAndDeviceId(ADMIN_ID, "device123").isPresent());
  }

}
