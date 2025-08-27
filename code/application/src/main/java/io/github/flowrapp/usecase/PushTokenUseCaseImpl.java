package io.github.flowrapp.usecase;

import io.github.flowrapp.model.PushToken;
import io.github.flowrapp.port.input.PushTokenUseCase;
import io.github.flowrapp.port.output.PushTokenOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.value.PushTokenRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushTokenUseCaseImpl implements PushTokenUseCase {

  private final PushTokenOutput pushTokenOutput;

  private final UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @Transactional
  @Override
  public PushToken create(PushTokenRequest request) {
    val currentUser = userSecurityContextHolderOutput.getCurrentUser();
    log.info("Creating push token for user: {}", currentUser.id());

    pushTokenOutput.findByUserAndDeviceId(currentUser.id(), request.deviceId())
        .ifPresent(pushToken -> {
          log.info("Device ID {} already exists, deleting old token: {}", request.deviceId(), pushToken);
          pushTokenOutput.deleteById(pushToken.id());
        });

    return pushTokenOutput.save(
        PushToken.fromRequest(request, currentUser));
  }

  @Override
  public void delete(String deviceId) {
    log.debug("Deleting push token for user: {}", deviceId);
    pushTokenOutput.deleteByDeviceId(deviceId);
  }

}
