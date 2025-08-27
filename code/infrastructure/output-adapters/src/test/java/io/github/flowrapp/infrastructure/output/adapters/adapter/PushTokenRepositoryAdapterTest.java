package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.PushTokenEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.PushTokenJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.PushTokenEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.Platform;
import io.github.flowrapp.model.PushToken;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class PushTokenRepositoryAdapterTest {

  @Mock
  private PushTokenJpaRepository pushTokenJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

  @Spy
  @InjectMocks
  private PushTokenEntityMapper pushTokenEntityMapper = spy(Mappers.getMapper(PushTokenEntityMapper.class));

  @InjectMocks
  private PushTokenRepositoryAdapter pushTokenRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void findByDeviceId(Integer userId, String deviceId) {
    // Given
    var pushTokenEntity = Instancio.of(PushTokenEntity.class)
        .generate(field("platform"), gen -> gen.oneOf(Platform.values()).asString())
        .create();

    when(pushTokenJpaRepository.findByUserIdAndDeviceId(userId, deviceId))
        .thenReturn(Optional.of(pushTokenEntity));

    // When
    Optional<PushToken> result = pushTokenRepositoryAdapter.findByUserAndDeviceId(userId, deviceId);

    // Then
    assertThat(result)
        .isPresent()
        .get()
        .isNotNull()
        .returns(pushTokenEntity.getId(), PushToken::id)
        .returns(pushTokenEntity.getToken(), PushToken::token)
        .returns(pushTokenEntity.getDeviceId(), PushToken::deviceId)
        .returns(pushTokenEntity.getUser().getId(), r -> r.user().id())
        .returns(pushTokenEntity.getPlatform(), r -> r.platform().name())
        .returns(pushTokenEntity.getCreatedAt(), PushToken::createdAt);

    verify(pushTokenJpaRepository).findByUserIdAndDeviceId(userId, deviceId);
    verify(pushTokenEntityMapper).infra2domain(any());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void save(PushToken pushToken) {
    // Given
    var pushTokenEntity = Instancio.of(PushTokenEntity.class)
        .generate(field("platform"), gen -> gen.oneOf(Platform.values()).asString())
        .create();

    when(pushTokenJpaRepository.save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(pushToken.id(), PushTokenEntity::getId)
        .returns(pushToken.token(), PushTokenEntity::getToken)
        .returns(pushToken.deviceId(), PushTokenEntity::getDeviceId)
        .returns(pushToken.user().id(), r -> r.getUser().getId())
        .returns(pushToken.platform().name(), PushTokenEntity::getPlatform)
        .returns(pushToken.createdAt(), PushTokenEntity::getCreatedAt))))
            .thenReturn(pushTokenEntity);

    // When
    PushToken result = pushTokenRepositoryAdapter.save(pushToken);

    // Then
    assertThat(result)
        .isNotNull()
        .returns(pushTokenEntity.getId(), PushToken::id)
        .returns(pushTokenEntity.getToken(), PushToken::token)
        .returns(pushTokenEntity.getDeviceId(), PushToken::deviceId)
        .returns(pushTokenEntity.getUser().getId(), r -> r.user().id())
        .returns(pushTokenEntity.getPlatform(), r -> r.platform().name())
        .returns(pushTokenEntity.getCreatedAt(), PushToken::createdAt);
  }

  @Test
  void deleteById() {
    // Given
    Integer id = 1;

    // When
    pushTokenRepositoryAdapter.deleteById(id);

    // Then
    verify(pushTokenJpaRepository).deleteById(id);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void deleteByDeviceId(Integer userId, String deviceId) {
    // Given

    // When
    pushTokenRepositoryAdapter.deleteByUserAndDeviceId(userId, deviceId);

    // Then
    verify(pushTokenJpaRepository).deleteByUserIdAndDeviceId(userId, deviceId);
  }
}
