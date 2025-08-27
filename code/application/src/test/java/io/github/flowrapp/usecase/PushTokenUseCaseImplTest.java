package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.model.PushToken;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.PushTokenOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.value.PushTokenRequest;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class PushTokenUseCaseImplTest {

  @Mock
  private PushTokenOutput pushTokenOutput;

  @Mock
  private UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @InjectMocks
  private PushTokenUseCaseImpl pushTokenUseCaseImpl;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void create_withNoPreviousToken(User user, PushToken pushToken) {
    // GIVEN
    var request = Instancio.of(PushTokenRequest.class)
        .generate(field("token"), gen -> gen.oneOf(UUID.randomUUID().toString()))
        .create();

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(user);
    when(pushTokenOutput.save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .matches(token -> token.id() == null)
        .matches(token -> token.user().equals(user))
        .matches(token -> token.token().equals(request.token()))
        .matches(token -> token.deviceId().equals(request.deviceId()))
        .matches(token -> token.platform().equals(request.platform()))
        .matches(token -> token.createdAt() != null))))
            .thenReturn(pushToken);

    // WHEN
    var result = pushTokenUseCaseImpl.create(request);

    // THEN
    assertThat(result)
        .isNotNull()
        .isEqualTo(pushToken);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void create_withPreviousToken(User user, PushToken previousToken, PushToken pushToken) {
    // GIVEN
    var request = Instancio.of(PushTokenRequest.class)
        .generate(field("token"), gen -> gen.oneOf(UUID.randomUUID().toString()))
        .create();

    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(user);
    when(pushTokenOutput.findByUserAndDeviceId(user.id(), request.deviceId()))
        .thenReturn(Optional.of(previousToken));
    when(pushTokenOutput.save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .matches(token -> token.id() == null)
        .matches(token -> token.user().equals(user))
        .matches(token -> token.token().toString().equals(request.token()))
        .matches(token -> token.deviceId().equals(request.deviceId()))
        .matches(token -> token.platform().equals(request.platform()))
        .matches(token -> token.createdAt() != null))))
            .thenReturn(pushToken);

    // WHEN
    var result = pushTokenUseCaseImpl.create(request);

    // THEN
    verify(pushTokenOutput).deleteById(previousToken.id());
    assertThat(result)
        .isNotNull()
        .isEqualTo(pushToken);
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void delete(User user, String deviceId) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(user);

    // WHEN
    pushTokenUseCaseImpl.delete(deviceId);

    // THEN
    verify(pushTokenOutput).deleteByUserAndDeviceId(user.id(), deviceId);
  }

}
