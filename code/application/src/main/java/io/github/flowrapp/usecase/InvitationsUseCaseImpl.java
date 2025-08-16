package io.github.flowrapp.usecase;

import java.util.List;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.port.input.InvitationsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationsUseCaseImpl implements InvitationsUseCase {

  @Override
  public Invitation acceptInvitation(String token) {
    return null;
  }

  @Override
  public void deleteInvitation(Integer businessId, Integer invitationId) {

  }

  @Override
  public Invitation createInvitation(InvitationCreationRequest invitationCreationRequest) {
    return null;
  }

  @Override
  public List<Invitation> getBusinessInvitations(Integer businessId, String status) {
    return List.of();
  }
}
