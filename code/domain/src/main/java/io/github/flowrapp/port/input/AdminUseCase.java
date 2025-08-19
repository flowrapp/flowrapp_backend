package io.github.flowrapp.port.input;

import io.github.flowrapp.value.UserCreationRequest;

public interface AdminUseCase {

  void createUser(UserCreationRequest userCreationRequest);

}
