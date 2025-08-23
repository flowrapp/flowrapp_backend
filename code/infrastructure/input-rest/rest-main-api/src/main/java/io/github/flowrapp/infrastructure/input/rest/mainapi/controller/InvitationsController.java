package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.infrastructure.apirest.users.api.InvitationsApi;
import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserFromInvitationRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.InvitationsDTOMapper;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.port.input.InvitationsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InvitationsController implements InvitationsApi {

  private final InvitationsUseCase invitationsUseCase;

  private final InvitationsDTOMapper invitationsDTOMapper;

  @Override
  public ResponseEntity<Void> acceptInvitation(String token) {
    invitationsUseCase.acceptInvitation(
        UUID.fromString(token));

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> registerUserFromInvitation(String token, RegisterUserFromInvitationRequestDTO registerRequestDTO) {
    invitationsUseCase.registerInvitation(
        invitationsDTOMapper.rest2domain(token, registerRequestDTO));

    return ResponseEntity.status(CREATED).build();
  }

  @Override
  public ResponseEntity<GetBusinessInvitations200ResponseInnerDTO> createBusinessInvitation(Integer businessId,
      CreateBusinessInvitationRequestDTO createBusinessInvitationRequestDTO) {

    val result = invitationsUseCase.createInvitation(
        invitationsDTOMapper.rest2domain(businessId, createBusinessInvitationRequestDTO));

    return ResponseEntity.status(CREATED)
        .body(invitationsDTOMapper.domain2rest(result));
  }

  @Override
  public ResponseEntity<Void> deleteBusinessInvitation(Integer businessId, Integer invitationId) {
    invitationsUseCase.deleteInvitation(businessId, invitationId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<GetBusinessInvitations200ResponseInnerDTO>> getBusinessInvitations(Integer businessId, String status) {
    return ResponseEntity.ok(
        invitationsDTOMapper.domain2rest(
            invitationsUseCase.getBusinessInvitations(businessId, this.parseInvitationStatus(status))));
  }

  @Override
  public ResponseEntity<List<GetBusinessInvitations200ResponseInnerDTO>> getUserInvitations(String status) {
    return ResponseEntity.ok(
        invitationsDTOMapper.domain2rest(
            invitationsUseCase.getUserInvitations(this.parseInvitationStatus(status))));
  }

  private InvitationStatus parseInvitationStatus(String status) {
    if (status == null) {
      return null;
    }

    try {
      return InvitationStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(BAD_REQUEST, "Invalid status: " + status);
    }
  }

}
