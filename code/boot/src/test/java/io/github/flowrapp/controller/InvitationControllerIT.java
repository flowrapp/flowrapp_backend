package io.github.flowrapp.controller;

import static io.github.flowrapp.TestUtils.basicAuth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.RequestEntity.post;

import java.util.UUID;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.apirest.users.model.AcceptInvitation200ResponseDTO;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
@InitDatabase
@Transactional
class InvitationControllerIT {

  public static final UUID token = UUID.fromString("23a30f35-7aa2-44cf-970a-54b22bfedcfa");

  public static final String BUSINESS_NAME = "Accept Invitation Business";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private BusinessJpaRepository businessJpaRepository;

  @Test
  @Sql("classpath:scripts/invitation_test_data.sql")
  void testAcceptInvitation() {
    var result = testRestTemplate.exchange(post("/api/v1/invitations/accept?token=" + token)
            .header("Authorization", basicAuth(DatabaseData.TEST_USER_MAIL, DatabaseData.TEST_USER_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .build(), AcceptInvitation200ResponseDTO.class);

    assertThat(result)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull();

    assertThat(businessJpaRepository.findByName(BUSINESS_NAME))
        .isPresent()
        .get()
        .extracting(get)

  }

}
