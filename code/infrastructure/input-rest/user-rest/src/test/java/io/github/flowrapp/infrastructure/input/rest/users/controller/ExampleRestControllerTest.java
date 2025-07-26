package io.github.flowrapp.infrastructure.input.rest.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Objects;

import io.github.flowrapp.infrastructure.input.rest.users.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.users.dto.UserResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.users.mapper.ExampleMapper;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.input.UserRequestUseCase;

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
class ExampleRestControllerTest {

  @Spy
  private ExampleMapper exampleMapper = Mappers.getMapper(ExampleMapper.class);

  @Mock
  private UserRequestUseCase userRequestUseCase;

  @InjectMocks
  private ExampleRestController exampleRestController;

  @ParameterizedTest
  @InstancioSource
  void getUser_returnsUser_whenFound(UserRequestDTO userRequestDTO, User user) {
    // GIVEN
    when(userRequestUseCase.findUser(argThat(argument ->
        Objects.equals(argument.name(), userRequestDTO.name()))))
        .thenReturn(user);

    // WHEN
    var userResponse = exampleRestController.getUser(userRequestDTO);

    // THEN
    assertThat(userResponse)
        .returns(user.name(), UserResponseDTO::name)
        .returns(user.dni(), UserResponseDTO::dni);
  }
}
