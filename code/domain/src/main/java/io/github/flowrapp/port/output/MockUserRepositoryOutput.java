package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.MockUser;

import org.jspecify.annotations.NonNull;

public interface MockUserRepositoryOutput {

  Optional<MockUser> findUserByName(@NonNull String user);

}
