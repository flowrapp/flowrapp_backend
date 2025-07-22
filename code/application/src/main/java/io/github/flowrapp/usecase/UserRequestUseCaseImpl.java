package io.github.flowrapp.usecase;

import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRequest;
import io.github.flowrapp.port.input.UserRequestUseCase;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRequestUseCaseImpl implements UserRequestUseCase {

    private final UserRepositoryOutput userRepositoryOutput;

    @Override
    public User findUser(UserRequest userRequest) {
        return this.userRepositoryOutput.findUserByName(userRequest.name()).get(); // TODO change for exception
    }

}
