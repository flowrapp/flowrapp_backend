package io.github.flowrapp.usecase;

import static io.github.flowrapp.model.config.Constants.ADMIN_USER_MAIL;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.model.User;
import io.github.flowrapp.model.value.UserCreationRequest;
import io.github.flowrapp.port.output.BusinessRepositoryOutput;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;
import io.github.flowrapp.port.output.UserRepositoryOutput;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class AdminUseCaseImplTest {

  @Mock
  private UserRepositoryOutput userRepositoryOutput;

  @Mock
  private BusinessRepositoryOutput businessRepositoryOutput;

  @Mock
  private InvitationRepositoryOutput invitationRepositoryOutput;

  @InjectMocks
  private AdminUseCaseImpl adminUseCase;

  @ParameterizedTest
  @InstancioSource
  void createUser(UserCreationRequest userCreationRequest, User adminUser) {
    // GIVEN
    when(userRepositoryOutput.findUserByEmail(ADMIN_USER_MAIL))
        .thenReturn(Optional.of(adminUser));
    when(userRepositoryOutput.existsByEmail(userCreationRequest.mail()))
        .thenReturn(false);

    when(userRepositoryOutput.save(argThat(argument -> argument.mail().equals(userCreationRequest.mail()))))
        .then(returnsFirstArg());
    when(businessRepositoryOutput.save(argThat(argument -> argument.name().equals(userCreationRequest.business().name()))))
        .then(returnsFirstArg());
    when(invitationRepositoryOutput.save(argThat(argument -> argument.invited().mail().equals(userCreationRequest.mail())
        && argument.business().name().equals(userCreationRequest.business().name()))))
            .then(returnsFirstArg());

    // WHEN
    adminUseCase.createUser(userCreationRequest);

    // THEN

  }
}
