package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.User;

import org.jspecify.annotations.NonNull;

/**
 * Output port for user repository operations. Provides methods to interact with user data storage.
 */
public interface UserRepositoryOutput {

  Optional<User> findUserByEmail(@NonNull String email);

  boolean existsByEmail(@NonNull String email);

  @NonNull User save(@NonNull User user);
}
