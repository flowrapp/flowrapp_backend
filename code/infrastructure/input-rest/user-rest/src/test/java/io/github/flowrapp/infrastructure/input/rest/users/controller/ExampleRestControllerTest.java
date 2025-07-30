package io.github.flowrapp.infrastructure.input.rest.users.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Objects;

import io.github.flowrapp.infrastructure.apirest.users.model.GetUser200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserRequestDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
  void getUser_returnsUser_whenFound(GetUserRequestDTO userRequestDTO, User user) {
    // GIVEN
    when(userRequestUseCase.findUser(
        argThat(argument -> Objects.equals(argument.name(), userRequestDTO.getName()))))
            .thenReturn(user);

    // WHEN
    var userResponse = exampleRestController.getUser(userRequestDTO);

    // THEN
    assertThat(userResponse)
        .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .returns(user.name(), GetUser200ResponseDTO::getName)
        .returns(user.dni(), GetUser200ResponseDTO::getDni);
  }
}
