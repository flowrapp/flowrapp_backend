package io.github.flowrapp.controller;

import static io.github.flowrapp.TestUtils.basicAuth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.RequestEntity.delete;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.infrastructure.apirest.users.model.AcceptInvitation200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.input.rest.config.GlobalControllerAdvice;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessJpaRepository;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.InvitationJpaRepository;
import io.github.flowrapp.model.InvitationStatus;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("test")
@InitDatabase
class InvitationControllerIT {

  public static final String VALID_TOKEN = "23a30f35-7aa2-44cf-970a-54b22bfedcfa";

  public static final String EXPIRED_TOKEN = "bdaf548b-ce82-41e0-a6c4-c7d3f78f6ea4";

  public static final String INVALID_TOKEN = "invalid-token-456";

  public static final Integer BUSINESS_ID = 1;

  public static final Integer INVITATION_ID = 1;

  public static final String ADMIN_EMAIL = "admin@admin.com";

  public static final String ADMIN_PASSWORD = "1234";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private BusinessJpaRepository businessJpaRepository;

  @Autowired
  private InvitationJpaRepository invitationJpaRepository;

  @MockitoSpyBean
  private GlobalControllerAdvice globalControllerAdvice;

  // ================================ ACCEPT INVITATION TESTS ================================

  @Test
  @Sql("classpath:scripts/invitation_test_data.sql")
  void testAcceptInvitation_Success() {
    var result = testRestTemplate.exchange(post("/api/v1/invitations/accept?token=" + VALID_TOKEN)
        .header(AUTHORIZATION, basicAuth(DatabaseData.TEST_USER_MAIL, DatabaseData.TEST_USER_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .build(), AcceptInvitation200ResponseDTO.class);

    assertThat(result)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull();

    // Verify invitation status changed to ACCEPTED in database
    var invitation = invitationJpaRepository.findByToken(UUID.fromString(VALID_TOKEN));
    assertThat(invitation)
        .isPresent()
        .get()
        .returns(InvitationStatus.ACCEPTED.name(), InvitationEntity::getStatus);
  }

  @Test
  @Sql("classpath:scripts/expired_invitation_test_data.sql")
  void testAcceptInvitation_ExpiredToken() {
    var result = testRestTemplate.exchange(post("/api/v1/invitations/accept?token=bdaf548b-ce82-41e0-a6c4-c7d3f78f6ea4")
        .header(AUTHORIZATION, basicAuth("expired@test.com", DatabaseData.TEST_USER_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .build(), AcceptInvitation200ResponseDTO.class);

    assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    this.verifyException(FunctionalError.INVITATION_EXPIRED);
  }

  @Test
  @Sql("classpath:scripts/invitation_test_data.sql")
  void testAcceptInvitation_InvalidToken() {
    var result = testRestTemplate.exchange(post("/api/v1/invitations/accept?token=83226856-21c3-4b41-9b29-31e4854a7f64")
        .header(AUTHORIZATION, basicAuth(DatabaseData.TEST_USER_MAIL, DatabaseData.TEST_USER_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .build(), String.class);

    assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    this.verifyException(FunctionalError.INVITATION_NOT_FOUND);
  }

  @Test
  @Sql("classpath:scripts/invitation_test_data.sql")
  void testAcceptInvitation_UnauthorizedUser() {
    var result = testRestTemplate.exchange(post("/api/v1/invitations/accept?token=" + VALID_TOKEN)
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .build(), String.class);

    assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    this.verifyException(FunctionalError.INVITATION_NOT_FOR_CURRENT_USER);
  }

  // ================================ CREATE INVITATION TESTS ================================

  @Test
  @Sql("classpath:scripts/create_invitation_test_data.sql")
  void testCreateInvitation_Success_ExistingUser() {
    var request = new CreateBusinessInvitationRequestDTO()
        .email("newuser@test.com")
        .role(CreateBusinessInvitationRequestDTO.RoleEnum.EMPLOYEE);

    var result = testRestTemplate.exchange(post("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request), GetBusinessInvitations200ResponseInnerDTO.class);

    assertThat(result)
        .isNotNull()
        .returns(CREATED, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .extracting(GetBusinessInvitations200ResponseInnerDTO::getEmail)
        .isEqualTo("newuser@test.com");
  }

  @Test
  @Sql("classpath:scripts/create_invitation_test_data.sql")
  void testCreateInvitation_Success_newUser() {
    var request = new CreateBusinessInvitationRequestDTO()
        .email("randomTesting@test.com")
        .role(CreateBusinessInvitationRequestDTO.RoleEnum.EMPLOYEE);

    var result = testRestTemplate.exchange(post("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request), GetBusinessInvitations200ResponseInnerDTO.class);

    assertThat(result)
        .isNotNull()
        .returns(CREATED, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .extracting(GetBusinessInvitations200ResponseInnerDTO::getEmail)
        .isEqualTo("randomTesting@test.com");
  }

  @Test
  @Sql("classpath:scripts/create_invitation_test_data.sql")
  void testCreateInvitation_DuplicateInvitation() {
    var request = new CreateBusinessInvitationRequestDTO()
        .email(DatabaseData.TEST_USER_MAIL)
        .role(CreateBusinessInvitationRequestDTO.RoleEnum.EMPLOYEE);

    var result = testRestTemplate.exchange(post("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request), Void.class);

    assertThat(result).returns(CONFLICT, ResponseEntity::getStatusCode);
    this.verifyException(FunctionalError.INVITATION_ALREADY_EXISTS);
  }

  @Test
  @Sql("classpath:scripts/create_invitation_test_data.sql")
  void testCreateInvitation_Conflict_UserAlreadyMember() {
    var request = new CreateBusinessInvitationRequestDTO()
        .email("duplicate@test.com")
        .role(CreateBusinessInvitationRequestDTO.RoleEnum.EMPLOYEE);

    var result = testRestTemplate.exchange(post("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request), Void.class);

    assertThat(result.getStatusCode()).isEqualTo(CONFLICT);
    this.verifyException(FunctionalError.USER_ALREADY_MEMBER_OF_BUSINESS);
  }

  @Test
  @Sql("classpath:scripts/create_invitation_test_data.sql")
  void testCreateInvitation_Forbidden_NotBusinessOwner() {
    var request = new CreateBusinessInvitationRequestDTO()
        .email("newuser@test.com")
        .role(CreateBusinessInvitationRequestDTO.RoleEnum.EMPLOYEE);

    var result = testRestTemplate.exchange(post("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(DatabaseData.TEST_USER_MAIL, DatabaseData.TEST_USER_PASSWORD))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request), String.class);

    assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    this.verifyException(FunctionalError.USER_INVITATION_NOT_OWNER);
  }

  // ================================ DELETE INVITATION TESTS ================================

  @Test
  @Sql("classpath:scripts/delete_invitation_test_data.sql")
  void testDeleteInvitation_Success() {
    var result = testRestTemplate.exchange(delete("/api/v1/businesses/2/invitations/3")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .build(), Void.class);

    assertThat(result.getStatusCode()).isEqualTo(NO_CONTENT);

    // Verify invitation was deleted from database
    var invitation = invitationJpaRepository.findByToken(UUID.fromString("85693920-d467-4c7b-8b01-ebe884003038"));
    assertThat(invitation).isEmpty();
  }

  @Test
  @Sql("classpath:scripts/delete_invitation_test_data.sql")
  void testDeleteInvitation_Forbidden_NotBusinessOwner() {
    var result = testRestTemplate.exchange(delete("/api/v1/businesses/2/invitations/3")
        .header(AUTHORIZATION, basicAuth(DatabaseData.TEST_USER_MAIL, DatabaseData.TEST_USER_PASSWORD))
        .build(), Void.class);

    assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    this.verifyException(FunctionalError.USER_INVITATION_NOT_OWNER);
  }

  @Test
  @Sql("classpath:scripts/delete_invitation_test_data.sql")
  void testDeleteInvitation_Forbidden_notPending() {
    var result = testRestTemplate.exchange(delete("/api/v1/businesses/2/invitations/5")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .build(), String.class);

    assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    this.verifyException(FunctionalError.INVITATION_NOT_PENDING);
  }

  @Test
  @Sql("classpath:scripts/delete_invitation_test_data.sql")
  void testDeleteInvitation_NotFound_InvalidInvitationId() {
    var result = testRestTemplate.exchange(delete("/api/v1/businesses/2/invitations/999")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .build(), String.class);

    assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    this.verifyException(FunctionalError.INVITATION_NOT_FOUND);
  }

  // ================================ GET BUSINESS INVITATIONS TESTS ================================

  @Test
  @Sql("classpath:scripts/get_invitations_test_data.sql")
  void testGetBusinessInvitations_Success_PendingStatus() {
    var result = testRestTemplate.exchange(get("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(ADMIN_EMAIL, ADMIN_PASSWORD))
        .build(), new ParameterizedTypeReference<List<GetBusinessInvitations200ResponseInnerDTO>>() {});

    assertThat(result)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(2);
  }

  @Test
  @Sql("classpath:scripts/get_invitations_test_data.sql")
  void testGetBusinessInvitations_Forbidden_NotBusinessOwner() {
    var result = testRestTemplate.exchange(get("/api/v1/businesses/2/invitations")
        .header(AUTHORIZATION, basicAuth(DatabaseData.TEST_USER_MAIL, DatabaseData.TEST_USER_PASSWORD))
        .build(), String.class);

    assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    this.verifyException(FunctionalError.USER_INVITATION_NOT_OWNER);
  }

  private void verifyException(FunctionalError error) {
    verify(globalControllerAdvice).handleFunctionalException(argThat(argument -> argument.getCode() == error.getCode()));
  }

}
