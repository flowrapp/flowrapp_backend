package io.github.flowrapp.usecase;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.model.value.MockUserRequest;
import io.github.flowrapp.port.input.UserRequestUseCase;
import io.github.flowrapp.port.output.MockUserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockUserRequestUseCaseImpl implements UserRequestUseCase {

  private final MockUserRepositoryOutput mockUserRepositoryOutput;

  @Override
  public MockUser findUser(MockUserRequest userRequest) {
    log.debug("Getting request for: {}", userRequest);

    return this.mockUserRepositoryOutput.findUserByName(userRequest.name())
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));
  }

}
