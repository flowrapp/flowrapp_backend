package io.github.flowrapp.port.input;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.model.value.InvitationRegistrationRequest;

public interface InvitationsUseCase {

  /** Accepts an invitation using the provided token. In this case, the user is already registered. */
  Invitation acceptInvitation(UUID token);

  /** Accepts an invitation from a user that is not registered yet. */
  Invitation registerInvitation(InvitationRegistrationRequest invitationRegistration);

  void deleteInvitation(Integer businessId, Integer invitationId);

  Invitation createInvitation(InvitationCreationRequest invitationCreationRequest);

  List<Invitation> getBusinessInvitations(Integer businessId, InvitationStatus status);
}
