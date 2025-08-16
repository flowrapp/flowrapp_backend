package io.github.flowrapp.port.input;

import java.util.List;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.value.InvitationCreationRequest;

public interface InvitationsUseCase {

  Invitation acceptInvitation(String token);

  void deleteInvitation(Integer businessId, Integer invitationId);

  Invitation createInvitation(InvitationCreationRequest invitationCreationRequest);

  List<Invitation> getBusinessInvitations(Integer businessId, String status);
}
