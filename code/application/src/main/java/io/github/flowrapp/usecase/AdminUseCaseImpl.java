package io.github.flowrapp.usecase;

import static io.github.flowrapp.model.config.Constants.ADMIN_USER_MAIL;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
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

    val invitation = createInvitation(newUser, newBusiness, adminUser);
    // TODO: send email to user with activation link

    log.debug("User created successfully: {}", newUser);
  }

  private User createNewUser(UserCreationRequest userCreationRequest) {
    log.debug("Creating new user with request: {}", userCreationRequest);

    val user = User.builder()
        .name(userCreationRequest.username())
        .mail(userCreationRequest.mail())
        .phone("") // TODO
        .passwordHash("")
        .enabled(false)
        .createdAt(OffsetDateTime.now())
        .build();

    return userRepositoryOutput.save(user);
  }

  private Business createNewBusiness(BusinessCreationRequest businessCreationRequest, User user) {
    log.debug("Creating new business with request: {}", businessCreationRequest);

    val newUser = Business.builder()
        .name(businessCreationRequest.name())
        .location(businessCreationRequest.location())
        .createdAt(OffsetDateTime.now())
        .owner(user)
        .build();

    return businessRepositoryOutput.save(newUser);
  }

  private Invitation createInvitation(User user, Business newBusiness, User adminUser) {
    log.debug("Creating invitation for user: {}", user);

    var invitation = Invitation.builder()
        .invited(user)
        .business(newBusiness)
        .invitedBy(adminUser)
        .token(UUID.randomUUID())
        .role(UserRole.ADMIN)
        .createdAt(OffsetDateTime.now())
        .expiresAt(OffsetDateTime.now().plusDays(7)) // TODO: make configurable
        .status(InvitationStatus.PENDING)
        .build();

    return invitationRepositoryOutput.save(invitation);
  }

}
