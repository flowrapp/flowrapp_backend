package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.UserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;

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
  @InstancioSource
  void findUserByEmail(String mail, UserEntity userEntity) {
    // Given
    when(userJpaRepository.findByMail(mail))
        .thenReturn(Optional.of(userEntity));

    // When
    var result = userRepositoryAdapter.findUserByEmail(mail);

    // Then
    assertTrue(result.isPresent());
    assertEquals(userEntityMapper.infra2domain(userEntity), result.get());
  }

}
