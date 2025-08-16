package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.infrastructure.apirest.users.api.InvitationsApi;
import io.github.flowrapp.infrastructure.apirest.users.model.AcceptInvitation200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.InvitationsDTOMapper;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.port.input.InvitationsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InvitationsController implements InvitationsApi {

  private final InvitationsUseCase invitationsUseCase;

  private final InvitationsDTOMapper invitationsDTOMapper;

  @Override
  public ResponseEntity<AcceptInvitation200ResponseDTO> acceptInvitation(String token) {
    val result = invitationsUseCase.acceptInvitation(UUID.fromString(token));

    return ResponseEntity.ok(
        invitationsDTOMapper.domain2restAccept(result));
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
    val result = invitationsUseCase.getBusinessInvitations(businessId, InvitationStatus.valueOf(status));

    return ResponseEntity.ok(
        invitationsDTOMapper.domain2rest(result));
  }
}
