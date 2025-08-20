package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.UserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.User;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserRepositoryAdapterTest {

  @Mock
  private UserJpaRepository userJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

  @InjectMocks
  private UserRepositoryAdapter userRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void findUserByEmail(String mail, UserEntity userEntity) {
    // Given
    when(userJpaRepository.findByMail(mail))
        .thenReturn(Optional.of(userEntity));

    // When
    var result = userRepositoryAdapter.findUserByEmail(mail);

    // Then
    assertThat(result)
        .isPresent()
        .get()
        .satisfies(user -> assertNotNull(user.id()))
        .returns(userEntity.getName(), User::name)
        .returns(userEntity.getMail(), User::mail)
        .returns(userEntity.getPhone(), User::phone)
        .returns(userEntity.getEnabled(), User::enabled)
        .returns(userEntity.getCreatedAt(), User::createdAt);
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void existsByEmail(String email) {
    // Given
    when(userJpaRepository.existsByMail(email))
        .thenReturn(true);

    // When
    boolean exists = userRepositoryAdapter.existsByEmail(email);

    // Then
    assertThat(exists).isTrue();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void save(User user, UserEntity userEntity) {
    // Given
    when(userJpaRepository.save(argThat(argument -> argument.getMail().equals(user.mail()))))
        .thenReturn(userEntity);

    // When
    User savedUser = userRepositoryAdapter.save(user);

    // Then
    assertNotNull(savedUser);
    verify(userJpaRepository).save(assertArg(argument -> assertThat(argument)
        .returns(user.name(), UserEntity::getName)
        .returns(user.mail(), UserEntity::getMail)
        .returns(user.phone(), UserEntity::getPhone)
        .returns(user.enabled(), UserEntity::getEnabled)
        .returns(user.createdAt(), UserEntity::getCreatedAt)));
  }

}
