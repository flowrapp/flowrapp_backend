package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.PushTokenJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.PushTokenEntityMapper;
import io.github.flowrapp.model.PushToken;
import io.github.flowrapp.port.output.PushTokenOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushTokenRepositoryAdapter implements PushTokenOutput {

  private final PushTokenJpaRepository pushTokenJpaRepository;

  private final PushTokenEntityMapper pushTokenEntityMapper;

  @Override
  public Optional<PushToken> findByUserAndDeviceId(Integer userId, String deviceId) {
    return pushTokenJpaRepository.findByUserIdAndDeviceId(userId, deviceId)
        .map(pushTokenEntityMapper::infra2domain);
  }

  @Override
  public PushToken save(PushToken pushToken) {
    return pushTokenEntityMapper.infra2domain(
        pushTokenJpaRepository.save(
            pushTokenEntityMapper.domain2Infra(pushToken)));
  }

  @Override
  public void deleteById(Integer id) {
    pushTokenJpaRepository.deleteById(id);
  }

  @Override
  public void deleteByDeviceId(String deviceId) {
    pushTokenJpaRepository.deleteByDeviceId(deviceId);
  }
}
