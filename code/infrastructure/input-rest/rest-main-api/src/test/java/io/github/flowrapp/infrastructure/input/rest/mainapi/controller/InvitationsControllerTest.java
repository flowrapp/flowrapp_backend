package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.AcceptInvitation200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.InvitationsDTOMapper;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.port.input.InvitationsUseCase;

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
  @InstancioSource
  void acceptInvitation(String token, Invitation invitation) {
    // GIVEN
    when(invitationsUseCase.acceptInvitation(token))
        .thenReturn(invitation);

    // WHEN
    var response = invitationsController.acceptInvitation(token);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(invitation.id(), AcceptInvitation200ResponseDTO::getInvitationId)
        .returns(invitation.business().id(), AcceptInvitation200ResponseDTO::getBusinessId)
        .returns(invitation.role().toString(), dto -> dto.getRole().toString());
  }

  @ParameterizedTest
  @InstancioSource
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
        .returns(invitation.expiresAt(), GetBusinessInvitations200ResponseInnerDTO::getExpiresAt)
        .returns(invitation.createdAt(), GetBusinessInvitations200ResponseInnerDTO::getCreatedAt);
  }

  @ParameterizedTest
  @InstancioSource
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
  @InstancioSource
  void getBusinessInvitations(Integer businessId, InvitationStatus status,
      List<Invitation> invitationList) {
    // GIVEN
    when(invitationsUseCase.getBusinessInvitations(businessId, status.toString()))
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

}
