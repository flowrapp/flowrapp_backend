package io.github.flowrapp.usecase;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUseCaseImpl implements AdminUseCase {

  private final UserRepositoryOutput userRepositoryOutput;

  private final BusinessRepositoryOutput businessRepositoryOutput;

  private final InvitationRepositoryOutput invitationRepositoryOutput;

  @Override
  public void createUser(UserCreationRequest userCreationRequest) {
    log.debug("Creating user: {}", userCreationRequest);

    if (userRepositoryOutput.existsByEmail(userCreationRequest.mail()))
        throw new FunctionalException(FunctionalError.USERNAME_ALREADY_EXISTS);

    val newUser = this.createNewUser(userCreationRequest);

    val invitation = createInvitation(newUser);
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
        .ownerBusinesses(List.of(
            this.createNewBusiness(userCreationRequest.business())))
        .build();

    return userRepositoryOutput.save(user);
  }

  private Business createNewBusiness(BusinessCreationRequest businessCreationRequest) {
    log.debug("Creating new business with request: {}", businessCreationRequest);

    return Business.builder()
        .name(businessCreationRequest.name())
        .location(businessCreationRequest.location())
        .createdAt(OffsetDateTime.now())
        .members(Collections.emptyList())
        .build();
  }

  private Invitation createInvitation(User user) {
    log.debug("Creating invitation for user: {}", user);

    var invitation = Invitation.builder()
        .invited(user)
        .business(user.ownerBusinesses().getFirst()) // The user only has one business at this point
        .invitedBy(user) // TODO: change for admin
        .token(UUID.randomUUID())
        .role(UserRole.ADMIN)
        .createdAt(OffsetDateTime.now())
        .expiresAt(OffsetDateTime.now().plusDays(7)) // TODO: make configurable
        .status(InvitationStatus.PENDING)
        .build();

    return invitationRepositoryOutput.save(invitation);
  }

}
