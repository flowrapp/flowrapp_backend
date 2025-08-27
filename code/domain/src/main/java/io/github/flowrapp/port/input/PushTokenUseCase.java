package io.github.flowrapp.port.input;

import io.github.flowrapp.model.PushToken;
import io.github.flowrapp.value.PushTokenRequest;

public interface PushTokenUseCase {

  PushToken create(PushTokenRequest request);

  void delete(String deviceId);

}
