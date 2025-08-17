package io.github.flowrapp.usecase;

import io.github.flowrapp.exception.FunctionalError;
import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.model.value.MockUserRequest;
import io.github.flowrapp.port.input.UserRequestUseCase;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.MockUserRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockUserRequestUseCaseImpl implements UserRequestUseCase {

  private final MockUserRepositoryOutput mockUserRepositoryOutput;

  private final UserRepositoryOutput userRepositoryOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  private final AuthCryptoPort authCryptoPort;

  @Override
  public MockUser findUser(MockUserRequest userRequest) {
    log.debug("Getting request for: {}", userRequest);

    return this.mockUserRepositoryOutput.findUserByName(userRequest.name())
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));
  }

  @Override
  public void changePassword(String password) {
    var currentUser = userSecurityContextHolderOutput.getCurrentUser()
        .orElseThrow(() -> new FunctionalException(FunctionalError.USER_NOT_FOUND));

    userRepositoryOutput.save(
        currentUser.withPasswordHash(authCryptoPort.hashPassword(password)));
  }

}
