package io.github.flowrapp.port.input;

import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRequest;

public interface UserRequestUseCase {

    User findUser(UserRequest userRequest);

}
