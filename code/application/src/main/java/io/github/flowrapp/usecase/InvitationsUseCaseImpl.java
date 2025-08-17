package io.github.flowrapp.usecase;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.port.input.InvitationsUseCase;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationsUseCaseImpl implements InvitationsUseCase {

  private final InvitationRepositoryOutput invitationRepositoryOutput;

  private final UserRepositoryOutput userRepositoryOutput;

  private final BusinessRepositoryOutput businessRepositoryOutput;

  private final BusinessUserRepositoryOutput businessUserRepositoryOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Transactional
  @Override
  public Invitation createInvitation(InvitationCreationRequest request) {
    log.debug("Creating invitation for: {}", request);

    val currentUser = this.getCurrentUser();
    val business = businessRepositoryOutput.findById(request.businessId())
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    if (!business.isOwner(currentUser)) {
      log.warn("User {} is not authorized to create invitation for business {}", currentUser.mail(), request.businessId());
      throw new FunctionalException(FunctionalError.USER_INVITATION_NOT_OWNER);
    }

    val user = userRepositoryOutput.findUserByEmail(request.email())
        .orElseGet(() -> {
          log.debug("Creating new user for invitation: {}", request);
          return userRepositoryOutput.save(User.fromMail(request.email()));
        });

    if (businessUserRepositoryOutput.userIsMemberOfBusiness(user.id(), business.id())) {
      throw new FunctionalException(FunctionalError.USER_ALREADY_MEMBER_OF_BUSINESS);
    }

    if (invitationRepositoryOutput.userIsAlreadyInvitedToBusiness(user.id(), business.id())) {
      throw new FunctionalException(FunctionalError.INVITATION_ALREADY_EXISTS);
    }

    val invitation = invitationRepositoryOutput.save(
        Invitation.create(user, business, currentUser, request.role()));

    log.debug("Created invitation: {}", invitation);
    // TODO: send email if !user.enabled. If not, send notification?? idk

    return invitation;
  }

  @Transactional
  @Override
  public Invitation acceptInvitation(UUID token) {
    log.debug("Accepting invitation with token: {}", token);

    val currentUser = this.getCurrentUser();
    val invitation = invitationRepositoryOutput.findByToken(token)
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVITATION_NOT_FOUND));

    if (!invitation.isInvited(currentUser)) {
      throw new FunctionalException(FunctionalError.INVITATION_NOT_FOR_CURRENT_USER);
    }

    if (invitation.hasExpired()) {
      throw new FunctionalException(FunctionalError.INVITATION_EXPIRED);
    }

    if (!invitation.isPending()) {
      throw new FunctionalException(FunctionalError.INVITATION_ALREADY_ACCEPTED);
    }

    log.debug("Creating business user from invitation: {}", invitation);
    invitationRepositoryOutput.save(invitation.accepted()); // Mark invitation as accepted
    businessUserRepositoryOutput.save(
        BusinessUser.fromInvitation(invitation));

    return invitation;
  }

  @Override
  public void deleteInvitation(Integer businessId, Integer invitationId) {
    log.debug("Deleting invitation for business {} with id {}", businessId, invitationId);

    val currentUser = this.getCurrentUser();
    val business = businessRepositoryOutput.findById(businessId)
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    if (!business.isOwner(currentUser)) {
      log.warn("User {} is not authorized to delete invitation for business {}", currentUser.mail(), businessId);
      throw new FunctionalException(FunctionalError.USER_INVITATION_NOT_OWNER);
    }

    var invitation = invitationRepositoryOutput.findById(invitationId)
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVITATION_NOT_FOUND));

    if (!invitation.isPending()) {
      log.warn("Cannot delete invitation {} for business {}: not in pending status", invitationId, businessId);
      throw new FunctionalException(FunctionalError.INVITATION_NOT_PENDING);
    }

    invitationRepositoryOutput.deleteInvitation(businessId, invitationId);
  }

  @Override
  public List<Invitation> getBusinessInvitations(Integer businessId, InvitationStatus status) {
    log.debug("Retrieving invitations for business {} with status {}", businessId, status);

    val currentUser = this.getCurrentUser();
    val business = businessRepositoryOutput.findById(businessId)
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    if (!business.isOwner(currentUser)) {
      log.warn("User {} is not authorized to retrieve invitations for business {}", currentUser.mail(), businessId);
      throw new FunctionalException(FunctionalError.USER_INVITATION_NOT_OWNER);
    }

    return invitationRepositoryOutput.findByBusinessIdAndStatus(businessId, status);
  }

  private User getCurrentUser() {
    return userSecurityContextHolderOutput.getCurrentUser()
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));
  }

}
