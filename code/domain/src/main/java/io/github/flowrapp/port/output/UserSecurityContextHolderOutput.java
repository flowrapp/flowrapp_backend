package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.User;

public interface UserSecurityContextHolderOutput {

  Optional<String> getCurrentUserEmail();

  Optional<User> getCurrentUser();

}
