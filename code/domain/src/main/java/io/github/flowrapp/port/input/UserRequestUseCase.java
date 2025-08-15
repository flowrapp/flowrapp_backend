package io.github.flowrapp.port.input;

import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.model.MockUserRequest;

public interface UserRequestUseCase {

  MockUser findUser(MockUserRequest userRequest);

}
