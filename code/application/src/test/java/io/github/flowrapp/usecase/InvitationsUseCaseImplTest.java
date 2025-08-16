package io.github.flowrapp.usecase;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class InvitationsUseCaseImplTest {

  @Mock
  private InvitationRepositoryOutput invitationRepositoryOutput;

  @Mock
  private BusinessUserRepositoryOutput businessUserRepositoryOutput;

  @InjectMocks
  private InvitationsUseCaseImpl invitationsUseCase;

  @Test
  void deleteInvitation(Integer businessId, Integer invitationId) {
    // GIVEN

    // WHEN
    invitationsUseCase.deleteInvitation(businessId, invitationId);

    verify(invitationRepositoryOutput).deleteInvitation(businessId, invitationId);
  }

  @Test
  void getBusinessInvitations(Integer businessId, InvitationStatus status, List<Invitation> invitationList) {
    // GIVEN
    when(invitationRepositoryOutput.findByBusinessIdAndStatus(businessId, status))
        .thenReturn(invitationList);

    // WHEN
    var result = invitationsUseCase.getBusinessInvitations(businessId, status);

    assertThat(result)
        .isNotEmpty()
        .hasSize(invitationList.size());
  }
}