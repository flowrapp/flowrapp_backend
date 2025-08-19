package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserFromInvitationRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.InvitationsDTOMapper;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.port.input.InvitationsUseCase;
import io.github.flowrapp.value.InvitationCreationRequest;
import io.github.flowrapp.value.InvitationRegistrationRequest;

import org.assertj.core.api.InstanceOfAssertFactories;
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
class InvitationsControllerTest {

  @Mock
  private InvitationsUseCase invitationsUseCase;

  @Spy
  private InvitationsDTOMapper invitationsDTOMapper = Mappers.getMapper(InvitationsDTOMapper.class);

  @InjectMocks
  private InvitationsController invitationsController;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void acceptInvitation(UUID token) {
    // GIVEN

    // WHEN
    var response = invitationsController.acceptInvitation(token.toString());

    // THEN
    assertThat(response).returns(OK, ResponseEntity::getStatusCode);
    verify(invitationsUseCase).acceptInvitation(token);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void registerUserFromInvitation(UUID token, RegisterUserFromInvitationRequestDTO registerRequestDTO) {
    // GIVEN

    // WHEN
    var response = invitationsController.registerUserFromInvitation(token.toString(), registerRequestDTO);

    // THEN
    assertThat(response).returns(CREATED, ResponseEntity::getStatusCode);
    verify(invitationsUseCase).registerInvitation(
        assertArg(argument -> assertThat(argument)
            .isNotNull()
            .returns(token, InvitationRegistrationRequest::token)
            .returns(registerRequestDTO.getUsername(), InvitationRegistrationRequest::username)
            .returns(registerRequestDTO.getPassword(), InvitationRegistrationRequest::password)));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createBusinessInvitation(Integer businessId, CreateBusinessInvitationRequestDTO creationDTO,
      Invitation invitation) {
    // GIVEN
    when(invitationsUseCase.createInvitation(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(businessId, InvitationCreationRequest::businessId)
        .returns(creationDTO.getEmail(), InvitationCreationRequest::email)
        .returns(creationDTO.getRole().toString(), dto -> dto.role().toString()))))
            .thenReturn(invitation);

    // WHEN
    var response = invitationsController.createBusinessInvitation(businessId, creationDTO);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(CREATED, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(invitation.id(), GetBusinessInvitations200ResponseInnerDTO::getId)
        .returns(invitation.invited().mail(), GetBusinessInvitations200ResponseInnerDTO::getEmail)
        .returns(invitation.status().toString(), dto -> dto.getStatus().toString())
        .returns(invitation.role().toString(), dto -> dto.getRole().toString())
        .returns(invitation.expiresAt().atOffset(ZoneOffset.UTC), GetBusinessInvitations200ResponseInnerDTO::getExpiresAt)
        .returns(invitation.createdAt().atOffset(ZoneOffset.UTC), GetBusinessInvitations200ResponseInnerDTO::getCreatedAt);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deleteBusinessInvitation(Integer businessId, Integer invitationId) {
    // GIVEN
    // No specific setup needed for this test

    // WHEN
    var response = invitationsController.deleteBusinessInvitation(businessId, invitationId);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(NO_CONTENT, ResponseEntity::getStatusCode);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessInvitations(Integer businessId, InvitationStatus status,
      List<Invitation> invitationList) {
    // GIVEN
    when(invitationsUseCase.getBusinessInvitations(businessId, status))
        .thenReturn(invitationList);

    // WHEN
    var response = invitationsController.getBusinessInvitations(businessId, status.toString());

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .isInstanceOf(List.class)
        .asInstanceOf(InstanceOfAssertFactories.list(Invitation.class))
        .hasSize(invitationList.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserInvitations(InvitationStatus status, List<Invitation> invitationList) {
    // GIVEN
    when(invitationsUseCase.getUserInvitations(status))
        .thenReturn(invitationList);

    // WHEN
    var response = invitationsController.getUserInvitations(status.toString());

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .isInstanceOf(List.class)
        .asInstanceOf(InstanceOfAssertFactories.list(Invitation.class))
        .hasSize(invitationList.size());
  }

}
