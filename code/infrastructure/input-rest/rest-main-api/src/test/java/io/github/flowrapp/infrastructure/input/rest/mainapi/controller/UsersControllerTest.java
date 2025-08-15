package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.util.Objects;

import io.github.flowrapp.infrastructure.apirest.users.model.GetUser200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.UserDTOMapper;
import io.github.flowrapp.model.MockUser;
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
import org.springframework.http.ResponseEntity;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UsersControllerTest {

  @Mock
  private UserRequestUseCase userRequestUseCase;

  @Spy
  private UserDTOMapper userDTOMapper = Mappers.getMapper(UserDTOMapper.class);

  @InjectMocks
  private UsersController usersController;

  @ParameterizedTest
  @InstancioSource
  void getUser(GetUserRequestDTO getUserRequestDTO, MockUser user) {
    // GIVEN
    when(userRequestUseCase.findUser(argThat(dto -> Objects.equals(dto.name(), getUserRequestDTO.getName()))))
        .thenReturn(user);

    // WHEN
    var response = usersController.getUser(getUserRequestDTO);

    // THEN
    assertThat(response)
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(user.name(), GetUser200ResponseDTO::getName)
        .returns(user.dni(), GetUser200ResponseDTO::getDni);
  }

}
