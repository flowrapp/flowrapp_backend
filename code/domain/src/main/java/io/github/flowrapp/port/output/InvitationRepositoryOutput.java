package io.github.flowrapp.port.output;

import io.github.flowrapp.model.Invitation;

public interface InvitationRepositoryOutput {

  Invitation save(Invitation invitation);

}
