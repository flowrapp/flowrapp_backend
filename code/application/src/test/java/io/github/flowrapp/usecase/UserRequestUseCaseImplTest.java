package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.MockUserRequest;
import io.github.flowrapp.port.output.AuthCryptoPort;
import io.github.flowrapp.port.output.MockUserRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserRequestUseCaseImplTest {

  @Mock
  private MockUserRepositoryOutput mockUserRepositoryOutput;

  @Mock
  private UserRepositoryOutput userRepositoryOutput;

  @Mock
  private UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Mock
  private AuthCryptoPort authCryptoPort;

  @InjectMocks
  private UserRequestUseCaseImpl userRequestUseCase;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void findUser_returnsUser_whenFound(MockUserRequest userRequest, MockUser user) {
    // GIVEN
    when(mockUserRepositoryOutput.findUserByName(userRequest.name()))
        .thenReturn(Optional.of(user));

    // WHEN
    var result = userRequestUseCase.findUser(userRequest);

    // THEN
    assertThat(result)
        .isNotNull()
        .isEqualTo(user);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void findUser_throwsException_whenNotFound(MockUserRequest userRequest) {
    // GIVEN
    when(mockUserRepositoryOutput.findUserByName(userRequest.name()))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThrows(FunctionalException.class, () -> userRequestUseCase.findUser(userRequest));
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void changePassword_OK(String password, User user, String hashPassword, User newUser) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(user);

    when(authCryptoPort.hashPassword(password))
        .thenReturn(hashPassword);

    when(userRepositoryOutput.save(argThat(argument -> argument.id().equals(user.id()))))
        .thenReturn(newUser);

    // WHEN
    userRequestUseCase.changePassword(password);

    // THEN
    verify(userRepositoryOutput).save(any());
  }

}
