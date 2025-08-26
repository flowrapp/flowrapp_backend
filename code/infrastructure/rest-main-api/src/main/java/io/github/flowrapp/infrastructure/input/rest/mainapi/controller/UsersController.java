package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import io.github.flowrapp.infrastructure.apirest.users.api.UsersApi;
import io.github.flowrapp.infrastructure.apirest.users.model.ChangePasswordRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.UserDTOMapper;
import io.github.flowrapp.port.input.UserRequestUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

  private final UserRequestUseCase userRequestUseCase;

  private final UserDTOMapper userDTOMapper;

  @Override
  public ResponseEntity<Void> changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
    userRequestUseCase.changePassword(changePasswordRequestDTO.getPassword());
    return ResponseEntity.ok().build();
  }
}
