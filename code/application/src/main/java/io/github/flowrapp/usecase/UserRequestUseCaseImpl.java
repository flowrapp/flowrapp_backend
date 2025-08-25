package io.github.flowrapp.usecase;

import io.github.flowrapp.port.input.UserRequestUseCase;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.value.SensitiveInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRequestUseCaseImpl implements UserRequestUseCase {

  private final UserRepositoryOutput userRepositoryOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  private final AuthCryptoPort authCryptoPort;

  @Override
  public void changePassword(String password) {
    var currentUser = userSecurityContextHolderOutput.getCurrentUser();

    userRepositoryOutput.save(
        currentUser.withPasswordHash(SensitiveInfo.of(
            authCryptoPort.hashPassword(password))));
  }

}
