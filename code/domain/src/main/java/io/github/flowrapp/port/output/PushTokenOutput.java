package io.github.flowrapp.port.output;

import java.util.Optional;

import io.github.flowrapp.model.PushToken;

public interface PushTokenOutput {

  Optional<PushToken> findByUserAndDeviceId(Integer userId, String deviceId);

  PushToken save(PushToken pushToken);

  void deleteById(Integer id);

  void deleteByUserAndDeviceId(Integer userId, String deviceId);
}
