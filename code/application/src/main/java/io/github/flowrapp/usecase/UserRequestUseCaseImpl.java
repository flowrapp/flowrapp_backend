package io.github.flowrapp.usecase;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
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
    log.debug("Getting request for: {}", userRequest);

    return this.userRepositoryOutput.findUserByName(userRequest.name())
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));
  }

}
