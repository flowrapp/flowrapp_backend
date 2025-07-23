package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.User;
import org.jspecify.annotations.NonNull;

public interface UserRepositoryOutput {

  Optional<User> findUserByName(@NonNull String user);

}
