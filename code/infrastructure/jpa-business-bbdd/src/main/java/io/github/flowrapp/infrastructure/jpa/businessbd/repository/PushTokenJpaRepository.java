package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.PushTokenEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushTokenJpaRepository extends JpaRepository<PushTokenEntity, Integer> {

  Optional<PushTokenEntity> findByUserIdAndDeviceId(Integer userId, String deviceId);

  void deleteByDeviceId(String deviceId);

}
