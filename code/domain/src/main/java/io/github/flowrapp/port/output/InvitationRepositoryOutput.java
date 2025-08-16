package io.github.flowrapp.port.output;

import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.model.Invitation;

import org.jspecify.annotations.NonNull;

public interface InvitationRepositoryOutput {

  Optional<Invitation> findByToken(@NonNull UUID token);

  @NonNull
  Invitation save(@NonNull Invitation invitation);

}
