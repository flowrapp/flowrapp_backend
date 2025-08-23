package io.github.flowrapp.port.output;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;

import org.jspecify.annotations.NonNull;

public interface InvitationRepositoryOutput {

  Optional<Invitation> findById(Integer invitationId);

  Optional<Invitation> findByToken(@NonNull UUID token);

  List<Invitation> findByBusinessIdAndStatus(@NonNull Integer businessId, InvitationStatus status);

  List<Invitation> findByUserAndStatus(Integer id, InvitationStatus invitationStatus);

  boolean userIsAlreadyInvitedToBusiness(Integer invitedUserId, Integer businessId);

  @NonNull
  Invitation save(@NonNull Invitation invitation);

  void deleteInvitation(@NonNull Integer businessId, @NonNull Integer invitationId);

}
