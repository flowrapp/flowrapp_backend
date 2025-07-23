package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.main.postgres.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.main.postgres.repository.UserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.AdapterMapper;
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

  @Spy
  private AdapterMapper adapterMapper = Mappers.getMapper(AdapterMapper.class);

  @Mock
  private UserJpaRepository userJpaRepository;

  @InjectMocks
  private UserRepositoryAdapter userRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource
  void findUserByName_returnsUser_whenFound(String name, UserEntity userEntity) {
    // GIVEN
    when(userJpaRepository.findByName(name))
        .thenReturn(Optional.of(userEntity));

    // WHEN
    Optional<User> result = userRepositoryAdapter.findUserByName(name);

    // THEN
    assertThat(result)
        .isNotNull()
        .isPresent()
        .get()
        .returns(userEntity.getName(), User::name)
        .returns(userEntity.getDni(), User::dni);
  }
}