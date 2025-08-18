package io.github.flowrapp.usecase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.model.value.InvitationRegistrationRequest;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class InvitationsUseCaseImplTest {

  @Mock
  private InvitationRepositoryOutput invitationRepositoryOutput;

  @Mock
  private BusinessUserRepositoryOutput businessUserRepositoryOutput;

  @Mock
  private BusinessRepositoryOutput businessRepositoryOutput;

  @Mock
  private UserRepositoryOutput userRepositoryOutput;

  @Mock
  private UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Mock
  private AuthCryptoPort authCryptoPort;

  @InjectMocks
  private InvitationsUseCaseImpl invitationsUseCase;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createInvitation_success(InvitationCreationRequest request, User currentUser, User invitedUser, Invitation savedInvitation) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(true);
    when(userRepositoryOutput.findUserByEmail(request.email())).thenReturn(Optional.of(invitedUser));
    when(businessUserRepositoryOutput.userIsMemberOfBusiness(invitedUser.id(), business.id())).thenReturn(false);
    when(invitationRepositoryOutput.userIsAlreadyInvitedToBusiness(invitedUser.id(), business.id())).thenReturn(false);
    when(invitationRepositoryOutput.save(any(Invitation.class))).thenReturn(savedInvitation);

    // WHEN
    Invitation result = invitationsUseCase.createInvitation(request);

    // THEN
    assertThat(result).isEqualTo(savedInvitation);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createInvitation_createsNewUser(InvitationCreationRequest request, User currentUser, User newUser, Invitation savedInvitation) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(true);
    when(userRepositoryOutput.findUserByEmail(request.email())).thenReturn(Optional.empty());
    when(userRepositoryOutput.save(any(User.class))).thenReturn(newUser);
    when(businessUserRepositoryOutput.userIsMemberOfBusiness(newUser.id(), business.id())).thenReturn(false);
    when(invitationRepositoryOutput.userIsAlreadyInvitedToBusiness(newUser.id(), business.id())).thenReturn(false);
    when(invitationRepositoryOutput.save(any(Invitation.class))).thenReturn(savedInvitation);

    // WHEN
    Invitation result = invitationsUseCase.createInvitation(request);

    // THEN
    assertThat(result).isEqualTo(savedInvitation);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createInvitation_businessNotFound(InvitationCreationRequest request, User currentUser) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.createInvitation(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createInvitation_notOwner(InvitationCreationRequest request, User currentUser) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(false);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.createInvitation(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createInvitation_userAlreadyMember(InvitationCreationRequest request, User currentUser, User invitedUser) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(true);
    when(userRepositoryOutput.findUserByEmail(request.email())).thenReturn(Optional.of(invitedUser));
    when(businessUserRepositoryOutput.userIsMemberOfBusiness(invitedUser.id(), business.id())).thenReturn(true);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.createInvitation(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void createInvitation_alreadyInvited(InvitationCreationRequest request, User currentUser, User invitedUser) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(request.businessId())).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(true);
    when(userRepositoryOutput.findUserByEmail(request.email())).thenReturn(Optional.of(invitedUser));
    when(businessUserRepositoryOutput.userIsMemberOfBusiness(invitedUser.id(), business.id())).thenReturn(false);
    when(invitationRepositoryOutput.userIsAlreadyInvitedToBusiness(invitedUser.id(), business.id())).thenReturn(true);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.createInvitation(request))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void acceptInvitation_success(UUID token, User currentUser, BusinessUser businessUser) {
    // GIVEN
    Invitation invitation = mock(Invitation.class);
    Invitation acceptedInvitation = mock(Invitation.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(invitationRepositoryOutput.findByToken(token)).thenReturn(Optional.of(invitation));
    when(invitation.isInvited(currentUser)).thenReturn(true);
    when(invitation.hasExpired()).thenReturn(false);
    when(invitation.isPending()).thenReturn(true);
    when(invitation.accepted()).thenReturn(acceptedInvitation);
    when(invitationRepositoryOutput.save(acceptedInvitation)).thenReturn(acceptedInvitation);
    when(businessUserRepositoryOutput.save(any(BusinessUser.class))).thenReturn(businessUser);

    // WHEN
    invitationsUseCase.acceptInvitation(token);

    // THEN
    verify(invitationRepositoryOutput).save(acceptedInvitation);
    verify(businessUserRepositoryOutput).save(any(BusinessUser.class));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void acceptInvitation_notFound(UUID token, User currentUser) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(invitationRepositoryOutput.findByToken(token)).thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.acceptInvitation(token))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void acceptInvitation_notForCurrentUser(UUID token, User currentUser) {
    // GIVEN
    Invitation invitation = mock(Invitation.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(invitationRepositoryOutput.findByToken(token)).thenReturn(Optional.of(invitation));
    when(invitation.isInvited(currentUser)).thenReturn(false);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.acceptInvitation(token))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void acceptInvitation_expired(UUID token, User currentUser) {
    // GIVEN
    Invitation invitation = mock(Invitation.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(invitationRepositoryOutput.findByToken(token)).thenReturn(Optional.of(invitation));
    when(invitation.isInvited(currentUser)).thenReturn(true);
    when(invitation.hasExpired()).thenReturn(true);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.acceptInvitation(token))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void acceptInvitation_alreadyAccepted(UUID token, User currentUser) {
    // GIVEN
    Invitation invitation = mock(Invitation.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(invitationRepositoryOutput.findByToken(token)).thenReturn(Optional.of(invitation));
    when(invitation.isInvited(currentUser)).thenReturn(true);
    when(invitation.hasExpired()).thenReturn(false);
    when(invitation.isPending()).thenReturn(false);

    // WHEN
    invitationsUseCase.acceptInvitation(token);

    // THEN
    verify(invitationRepositoryOutput, never()).save(any(Invitation.class));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void registerInvitation_success(InvitationRegistrationRequest invitationRequest) {
    // GIVEN
    Invitation invitation = Instancio.of(Invitation.class)
        .set(field(Invitation::status), InvitationStatus.PENDING)
        .generate(field(Invitation::expiresAt), gen -> gen.temporal().instant().future())
        .set(field(User::enabled), false)
        .create();

    when(invitationRepositoryOutput.findByToken(invitationRequest.token()))
        .thenReturn(Optional.of(invitation));
    when(authCryptoPort.hashPassword(invitationRequest.password()))
        .thenReturn("hashedPassword");
    when(userRepositoryOutput.save(argThat(argument -> Objects.equals(argument.id(), invitation.invited().id()))))
        .then(returnsFirstArg());
    when(businessUserRepositoryOutput.save(argThat(argument -> Objects.equals(argument.user().id(), invitation.invited().id())
        && Objects.equals(argument.business().id(), invitation.business().id()))))
            .then(returnsFirstArg());
    when(invitationRepositoryOutput.save(argThat(argument -> Objects.equals(argument.invited().id(), invitation.invited().id()))))
        .then(returnsFirstArg());

    // WHEN
    invitationsUseCase.registerInvitation(invitationRequest);

    // THEN
    verify(invitationRepositoryOutput).save(argThat(argument -> Objects.equals(argument.id(), invitation.id())));
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void registerInvitation_notFound(InvitationRegistrationRequest invitationRequest) {
    // GIVEN
    when(invitationRepositoryOutput.findByToken(invitationRequest.token()))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.registerInvitation(invitationRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void registerInvitation_userAlreadyEnabled(InvitationRegistrationRequest invitationRequest) {
    // GIVEN
    Invitation invitation = Instancio.of(Invitation.class)
        .set(field(Invitation::status), InvitationStatus.PENDING)
        .generate(field(Invitation::expiresAt), gen -> gen.temporal().instant().future())
        .set(field(User::enabled), true)
        .create();

    when(invitationRepositoryOutput.findByToken(invitationRequest.token()))
        .thenReturn(Optional.of(invitation));

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.registerInvitation(invitationRequest))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deleteInvitation_success(Integer businessId, Integer invitationId, User currentUser) {
    // GIVEN
    Business business = mock(Business.class);
    Invitation invitation = mock(Invitation.class);

    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.of(business));
    when(invitationRepositoryOutput.findById(invitationId)).thenReturn(Optional.of(invitation));

    when(invitation.isPending()).thenReturn(true);
    when(business.isOwner(currentUser)).thenReturn(true);

    // WHEN
    invitationsUseCase.deleteInvitation(businessId, invitationId);

    // THEN
    verify(invitationRepositoryOutput).deleteInvitation(businessId, invitationId);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deleteInvitation_businessNotFound(Integer businessId, Integer invitationId, User currentUser) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.deleteInvitation(businessId, invitationId))
        .isInstanceOf(FunctionalException.class);

    verify(invitationRepositoryOutput, never()).deleteInvitation(businessId, invitationId);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deleteInvitation_notOwner(Integer businessId, Integer invitationId, User currentUser) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(false);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.deleteInvitation(businessId, invitationId))
        .isInstanceOf(FunctionalException.class);

    verify(invitationRepositoryOutput, never()).deleteInvitation(businessId, invitationId);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deleteInvitation_notPending(Integer businessId, Integer invitationId, User currentUser) {
    // GIVEN
    Business business = mock(Business.class);
    Invitation invitation = mock(Invitation.class);

    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.of(business));
    when(invitationRepositoryOutput.findById(invitationId)).thenReturn(Optional.of(invitation));

    when(invitation.isPending()).thenReturn(false);
    when(business.isOwner(currentUser)).thenReturn(true);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.deleteInvitation(businessId, invitationId))
        .isInstanceOf(FunctionalException.class);

    verify(invitationRepositoryOutput, never()).deleteInvitation(businessId, invitationId);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessInvitations_success(Integer businessId, InvitationStatus status, User currentUser, List<Invitation> invitations) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(true);
    when(invitationRepositoryOutput.findByBusinessIdAndStatus(businessId, status)).thenReturn(invitations);

    // WHEN
    List<Invitation> result = invitationsUseCase.getBusinessInvitations(businessId, status);

    // THEN
    assertThat(result).isEqualTo(invitations);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessInvitations_businessNotFound(Integer businessId, InvitationStatus status, User currentUser) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.empty());

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.getBusinessInvitations(businessId, status))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessInvitations_notOwner(Integer businessId, InvitationStatus status, User currentUser) {
    // GIVEN
    Business business = mock(Business.class);
    when(userSecurityContextHolderOutput.getCurrentUser()).thenReturn(currentUser);
    when(businessRepositoryOutput.findById(businessId)).thenReturn(Optional.of(business));
    when(business.isOwner(currentUser)).thenReturn(false);

    // WHEN / THEN
    assertThatThrownBy(() -> invitationsUseCase.getBusinessInvitations(businessId, status))
        .isInstanceOf(FunctionalException.class);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserInvitations_success(InvitationStatus status, User currentUser, List<Invitation> invitations) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(currentUser);
    when(invitationRepositoryOutput.findByUserAndStatus(currentUser.id(), status))
        .thenReturn(invitations);

    // WHEN
    List<Invitation> result = invitationsUseCase.getUserInvitations(status);

    // THEN
    assertThat(result)
        .isNotNull()
        .hasSize(invitations.size());
  }

}
