package io.github.flowrapp.usecase;

import static io.github.flowrapp.model.config.Constants.ADMIN_USER_MAIL;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRole;
import io.github.flowrapp.model.value.BusinessCreationRequest;
import io.github.flowrapp.model.value.UserCreationRequest;
import io.github.flowrapp.port.input.AdminUseCase;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUseCaseImpl implements AdminUseCase {

  private final UserRepositoryOutput userRepositoryOutput;

  private final BusinessRepositoryOutput businessRepositoryOutput;

  private final InvitationRepositoryOutput invitationRepositoryOutput;

  @Transactional
  @Override
  public void createUser(UserCreationRequest userCreationRequest) {
    log.debug("Creating user: {}", userCreationRequest);

    val adminUser = userRepositoryOutput.findUserByEmail(ADMIN_USER_MAIL)
        .orElseThrow(() -> new FunctionalException(FunctionalError.ADMIN_USER_NOT_FOUND));

    if (userRepositoryOutput.existsByEmail(userCreationRequest.mail())) {
      throw new FunctionalException(FunctionalError.USERNAME_ALREADY_EXISTS);
    }

    val newUser = this.createNewUser(userCreationRequest);
    val newBusiness = this.createNewBusiness(userCreationRequest.business(), newUser);

    val invitation = this.createInvitation(newUser, newBusiness, adminUser);
    // TODO: send email to user with activation link

    log.debug("User created successfully: {}", newUser);
  }

  private User createNewUser(UserCreationRequest userCreationRequest) {
    return userRepositoryOutput.save(
        User.fromUserCreationRequest(userCreationRequest));
  }

  private Business createNewBusiness(BusinessCreationRequest businessCreationRequest, User user) {
    log.debug("Creating business: {} for user {}", businessCreationRequest, user.mail());

    return businessRepositoryOutput.save(
        Business.fromBusinessCreationRequest(businessCreationRequest, user));
  }

  private Invitation createInvitation(User user, Business newBusiness, User adminUser) {
    log.debug("Creating invitation for user {} to business {}", user.mail(), newBusiness.name());

    return invitationRepositoryOutput.save(
        Invitation.createInvitation(user, newBusiness, adminUser, UserRole.ADMIN));
  }

}
