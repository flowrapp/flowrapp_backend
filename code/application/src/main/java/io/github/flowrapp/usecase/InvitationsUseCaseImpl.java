package io.github.flowrapp.usecase;

import java.util.List;
import java.util.UUID;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.input.InvitationsUseCase;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.value.InvitationCreationRequest;
import io.github.flowrapp.value.InvitationRegistrationRequest;
import io.github.flowrapp.value.SensitiveInfo;

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

  private final AuthCryptoPort authCryptoPort;

  @Transactional
  @Override
  public Invitation createInvitation(InvitationCreationRequest request) {
    log.debug("Creating invitation for: {}", request);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
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
  public void acceptInvitation(UUID token) {
    log.debug("Accepting invitation with token: {}", token);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val invitation = invitationRepositoryOutput.findByToken(token)
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVITATION_NOT_FOUND));

    if (!invitation.isInvited(currentUser)) {
      throw new FunctionalException(FunctionalError.INVITATION_NOT_FOR_CURRENT_USER);
    }

    if (invitation.hasExpired()) {
      throw new FunctionalException(FunctionalError.INVITATION_EXPIRED);
    }

    if (!invitation.isPending()) {
      log.warn("Cannot accept invitation {}: not in pending status", token);
      return; // No need to throw an exception, make it idempotent
    }

    log.debug("Creating business user from invitation: {}", invitation);
    businessUserRepositoryOutput.save(
        BusinessUser.fromInvitation(invitation));

    invitationRepositoryOutput.save(
        invitation.accepted()); // Mark invitation as accepted
  }

  @Override
  public void registerInvitation(InvitationRegistrationRequest invitationRegistration) {
    log.debug("Registering invitation: {}", invitationRegistration);

    val invitation = invitationRepositoryOutput.findByToken(invitationRegistration.token())
        .orElseThrow(() -> new FunctionalException(FunctionalError.INVITATION_NOT_FOUND));

    if (invitation.invited().enabled()) {
      log.warn("Cannot register invitation {}: user is already enabled: {}", invitationRegistration.token(), invitation.invited().mail());
      throw new FunctionalException(FunctionalError.USER_ALREADY_ENABLED);
    }

    if (invitation.hasExpired()) {
      throw new FunctionalException(FunctionalError.INVITATION_EXPIRED);
    }

    if (!invitation.isPending()) {
      throw new FunctionalException(FunctionalError.INVITATION_ALREADY_ACCEPTED);
    }

    val updatedUser = userRepositoryOutput.save(
        updateUser(invitationRegistration, invitation.invited()));
    val updatedInvitation = invitation.withInvited(updatedUser);

    businessUserRepositoryOutput.save(
        BusinessUser.fromInvitation(updatedInvitation));

    invitationRepositoryOutput.save(
        updatedInvitation.accepted());
  }

  private User updateUser(InvitationRegistrationRequest invitationRegistration, User invited) {
    return invited.toBuilder()
        .name(invitationRegistration.username())
        .phone(invitationRegistration.phone())
        .passwordHash(SensitiveInfo.of(
            authCryptoPort.hashPassword(invitationRegistration.password())))
        .enabled(true)
        .build();
  }

  @Override
  public void deleteInvitation(Integer businessId, Integer invitationId) {
    log.debug("Deleting invitation for business {} with id {}", businessId, invitationId);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
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

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    val business = businessRepositoryOutput.findById(businessId)
        .orElseThrow(() -> new FunctionalException(FunctionalError.BUSINESS_NOT_FOUND));

    if (!business.isOwner(currentUser)) {
      log.warn("User {} is not authorized to retrieve invitations for business {}", currentUser.mail(), businessId);
      throw new FunctionalException(FunctionalError.USER_INVITATION_NOT_OWNER);
    }

    return invitationRepositoryOutput.findByBusinessIdAndStatus(businessId, status);
  }

  @Override
  public List<Invitation> getUserInvitations(InvitationStatus invitationStatus) {
    log.debug("Retrieving invitations for user with status {}", invitationStatus);

    val currentUser = userSecurityContextHolderOutput.getCurrentUser();

    return invitationRepositoryOutput.findByUserAndStatus(currentUser.id(), invitationStatus);
  }

}
