package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import io.github.flowrapp.infrastructure.apirest.users.model.ChangePasswordRequestDTO;
import io.github.flowrapp.port.input.UserRequestUseCase;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UsersControllerTest {

  @Mock
  private UserRequestUseCase userRequestUseCase;

  @InjectMocks
  private UsersController usersController;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
    // GIVEN

    // WHEN
    var response = usersController.changePassword(changePasswordRequestDTO);

    // THEN
    assertThat(response)
        .returns(OK, ResponseEntity::getStatusCode);
  }

}
