package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.MockUserEntity;
import io.github.flowrapp.infrastructure.jpa.neonazure.repository.MockUserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.AdapterMapper;
import io.github.flowrapp.model.MockUser;

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
class UserJpaRepositoryAdapterTest {

  @Spy
  private AdapterMapper adapterMapper = Mappers.getMapper(AdapterMapper.class);

  @Mock
  private MockUserJpaRepository mockUserJpaRepository;

  @InjectMocks
  private MockUserRepositoryAdapter userRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource
  void findUserByName_returnsUser_whenFound(String name, MockUserEntity mockUserEntity) {
    // GIVEN
    when(mockUserJpaRepository.findByName(name))
        .thenReturn(Optional.of(mockUserEntity));

    // WHEN
    Optional<MockUser> result = userRepositoryAdapter.findUserByName(name);

    // THEN
    assertThat(result)
        .isNotNull()
        .isPresent()
        .get()
        .returns(mockUserEntity.getName(), MockUser::name)
        .returns(mockUserEntity.getDni(), MockUser::dni);
  }
}
