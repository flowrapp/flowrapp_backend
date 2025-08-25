package io.github.flowrapp.controller;

import static io.github.flowrapp.TestUtils.basicAuth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.RequestEntity.post;

import java.util.Collections;
import java.util.List;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestBusinessInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestBusinessInnerLocationDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestDTO;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessJpaRepository;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessUserJpaRepository;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.InvitationJpaRepository;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.UserJpaRepository;
import io.github.flowrapp.port.output.MailSenderPort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
@InitDatabase
class AdminControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private BusinessJpaRepository businessJpaRepository;

  @Autowired
  private BusinessUserJpaRepository businessUserJpaRepository;

  @Autowired
  private InvitationJpaRepository invitationJpaRepository;

  @MockitoBean
  private MailSenderPort mailSenderPort; // mock mail sender to avoid sending real emails during tests

  @Test
  void shouldRegisterUserSuccessfully() {
    // Given
    long initialUserCount = userJpaRepository.count();
    long initialBusinessCount = businessJpaRepository.count();
    long initialInvitationCount = invitationJpaRepository.count();

    RegisterUserRequestBusinessInnerLocationDTO locationDTO = new RegisterUserRequestBusinessInnerLocationDTO()
        .latitude(40.4168)
        .longitude(-3.7038)
        .area(100.0);

    RegisterUserRequestBusinessInnerDTO businessDTO = new RegisterUserRequestBusinessInnerDTO()
        .name("Test Company")
        .zone("Europe/Madrid")
        .location(locationDTO);

    RegisterUserRequestDTO requestDTO = new RegisterUserRequestDTO()
        .username("newTestUser")
        .mail("newuser@test.com")
        .business(Collections.singletonList(businessDTO));

    // When
    ResponseEntity<Void> response = testRestTemplate.exchange(
        post("/api/v1/admin/register")
            .header(AUTHORIZATION, basicAuth(DatabaseData.ADMIN_MAIL, DatabaseData.ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestDTO),
        Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(CREATED);

    // Verify user was created
    List<UserEntity> users = userJpaRepository.findAll();
    assertThat(users).hasSize((int) initialUserCount + 1);

    UserEntity createdUser = userJpaRepository.findByMail("newuser@test.com").orElse(null);
    assertThat(createdUser).isNotNull();
    assertThat(createdUser.getName()).isEqualTo("newTestUser");

    // Verify business was created
    List<BusinessEntity> businesses = businessJpaRepository.findAll();
    assertThat(businesses).hasSize((int) initialBusinessCount + 1);

    BusinessEntity createdBusiness = businessJpaRepository.findByName("Test Company").orElse(null);
    assertThat(createdBusiness).isNotNull();
    assertThat(createdBusiness.getLongitude()).isEqualTo(-3.7038);
    assertThat(createdBusiness.getLatitude()).isEqualTo(40.4168);
    assertThat(createdBusiness.getArea()).isEqualTo(100.0);
    assertThat(createdBusiness.getZone().toString()).isEqualTo("Europe/Madrid");
    assertThat(createdBusiness.getOwner().getId()).isEqualTo(createdUser.getId());

    // Verify invitation was created
    List<InvitationEntity> invitations = invitationJpaRepository.findAll();
    assertThat(invitations).hasSize((int) initialInvitationCount + 1);

    InvitationEntity createdInvitation = invitationJpaRepository.findAllByInvited_IdAndStatus(createdUser.getId(), "ACCEPTED")
        .stream()
        .findFirst()
        .orElse(null);

    assertThat(createdInvitation).isNotNull();
    assertThat(createdInvitation.getBusiness().getId()).isEqualTo(createdBusiness.getId());
    assertThat(createdInvitation.getRole()).isEqualTo("OWNER");
  }
}
