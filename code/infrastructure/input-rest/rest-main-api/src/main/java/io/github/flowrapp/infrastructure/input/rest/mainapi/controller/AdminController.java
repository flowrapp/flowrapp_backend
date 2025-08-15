package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import io.github.flowrapp.infrastructure.apirest.users.api.AdminApi;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.AdminDTOMapper;
import io.github.flowrapp.port.input.AdminUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

  private final AdminUseCase adminUseCase;

  private final AdminDTOMapper adminDTOMapper;

  @Override
  public ResponseEntity<Void> registerUser(RegisterUserRequestDTO registerUserRequestDTO) {
    adminUseCase.createUser(
        adminDTOMapper.rest2domain(registerUserRequestDTO));

    return ResponseEntity.ok().build();
  }

}
