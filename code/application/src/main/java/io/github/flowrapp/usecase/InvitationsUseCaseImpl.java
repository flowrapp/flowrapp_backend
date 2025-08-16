package io.github.flowrapp.usecase;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.port.input.InvitationsUseCase;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationsUseCaseImpl implements InvitationsUseCase {

  private final InvitationRepositoryOutput invitationRepositoryOutput;

  private final BusinessUserRepositoryOutput businessUserRepositoryOutput;

  @Override
  public Invitation createInvitation(InvitationCreationRequest invitationCreationRequest) {
    return null;
  }

  @Override
  public Invitation acceptInvitation(UUID token) {
    log.debug("Accepting invitation with token: {}", token);

    val invitation = invitationRepositoryOutput.save(
        invitationRepositoryOutput.findByToken(token)
            .orElseThrow(() -> new FunctionalException(FunctionalError.INVITATION_NOT_FOUND))
            .accepted());

    log.debug("Creating business user from invitation: {}", invitation);
    businessUserRepositoryOutput.save(
        BusinessUser.fromInvitation(invitation));

    return invitation;
  }

  @Override
  public void deleteInvitation(Integer businessId, Integer invitationId) {
    log.debug("Deleting invitation for business {} with id {}", businessId, invitationId);
    invitationRepositoryOutput.deleteInvitation(businessId, invitationId);
  }

  @Override
  public List<Invitation> getBusinessInvitations(Integer businessId, InvitationStatus status) {
    log.debug("Retrieving invitations for business {} with status {}", businessId, status);
    return invitationRepositoryOutput.findByBusinessIdAndStatus(businessId, status);
  }
}
