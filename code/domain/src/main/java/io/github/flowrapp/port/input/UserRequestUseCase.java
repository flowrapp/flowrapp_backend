package io.github.flowrapp.port.input;

import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.model.value.MockUserRequest;

public interface UserRequestUseCase {

  MockUser findUser(MockUserRequest userRequest);

}
