package io.github.flowrapp.port.output;

import io.github.flowrapp.model.User;

import java.util.Optional;

public interface UserRepositoryOutput {

    Optional<User> findUserByName(String user);

}
