package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.exception.FunctionalException;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRequest;
import io.github.flowrapp.port.output.UserRepositoryOutput;

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
  private UserRepositoryOutput userRepositoryOutput;

  @InjectMocks
  private UserRequestUseCaseImpl userRequestUseCase;

  @ParameterizedTest
  @InstancioSource
  void findUser_returnsUser_whenFound(UserRequest userRequest, User user) {
    // GIVEN
    when(userRepositoryOutput.findUserByName(userRequest.name()))
        .thenReturn(Optional.of(user));

    // WHEN
    var result = userRequestUseCase.findUser(userRequest);

    // THEN
    assertThat(result)
        .isNotNull()
        .isEqualTo(user);
  }

  @ParameterizedTest
  @InstancioSource
  void findUser_throwsException_whenNotFound(UserRequest userRequest) {
    // GIVEN
    when(userRepositoryOutput.findUserByName(userRequest.name()))
        .thenReturn(Optional.empty());

    // WHEN / THEN
    assertThrows(FunctionalException.class, () -> userRequestUseCase.findUser(userRequest));
  }

}
