package io.github.flowrapp.usecase;

import static io.github.flowrapp.config.Constants.ADMIN_USER_MAIL;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.BusinessUserRole;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.input.AdminUseCase;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.value.BusinessCreationRequest;
import io.github.flowrapp.value.MailEvent.OwnerCreationMailEvent;
import io.github.flowrapp.value.SensitiveInfo;
import io.github.flowrapp.value.UserCreationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUseCaseImpl implements AdminUseCase {

  private final UserRepositoryOutput userRepositoryOutput;

  private final BusinessRepositoryOutput businessRepositoryOutput;

  private final BusinessUserRepositoryOutput businessUserRepositoryOutput;

  private final InvitationRepositoryOutput invitationRepositoryOutput;

  private final AuthCryptoPort authCryptoPort;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public void createUser(UserCreationRequest userCreationRequest) {
    log.debug("Creating user: {}", userCreationRequest);

    val adminUser = userRepositoryOutput.findUserByEmail(ADMIN_USER_MAIL)
        .orElseThrow(() -> new FunctionalException(FunctionalError.ADMIN_USER_NOT_FOUND));

    if (userRepositoryOutput.existsByEmail(userCreationRequest.mail())) {
      throw new FunctionalException(FunctionalError.USERNAME_ALREADY_EXISTS);
    }

    val randomPassword = authCryptoPort.randomPassword();
    val newUser = this.createNewUser(userCreationRequest, randomPassword);
    val newBusiness = this.createNewBusiness(userCreationRequest.business(), newUser);

    val invitation = this.createInvitation(newUser, newBusiness, adminUser);
    businessUserRepositoryOutput.save( // Create role OWNER for the user in the new business
        BusinessUser.fromInvitation(invitation));

    applicationEventPublisher.publishEvent(
        new OwnerCreationMailEvent(invitation, randomPassword)); // Send mail to the new user with its credentials

    log.debug("User created successfully: {}", newUser);
  }

  private User createNewUser(UserCreationRequest userCreationRequest, String randomPassword) {
    val newUser = User.fromUserCreationRequest(userCreationRequest).toBuilder()
        .enabled(true) // Users created manually by admin are enabled by default
        .passwordHash(SensitiveInfo.of(authCryptoPort.hashPassword(randomPassword)))
        .build();

    return userRepositoryOutput.save(newUser); // Hash password
  }

  private Business createNewBusiness(BusinessCreationRequest businessCreationRequest, User user) {
    log.debug("Creating business: {} for user {}", businessCreationRequest, user.mail());

    return businessRepositoryOutput.save(
        Business.fromBusinessCreationRequest(businessCreationRequest, user));
  }

  private Invitation createInvitation(User user, Business newBusiness, User adminUser) {
    log.debug("Creating invitation for user {} to business {}", user.mail(), newBusiness.name());

    return invitationRepositoryOutput.save(
        Invitation.create(user, newBusiness, adminUser, BusinessUserRole.OWNER).accepted()); // Directly accept the invitation
  }

}
