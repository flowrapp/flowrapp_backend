package io.github.flowrapp.infrastructure.input.rest.users.controller;

import io.github.flowrapp.infrastructure.apirest.users.api.UsersApi;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUser200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.users.mapper.ExampleMapper;
import io.github.flowrapp.port.input.UserRequestUseCase;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExampleRestController implements UsersApi {

  private final ExampleMapper exampleMapper;

  private final UserRequestUseCase userRequestUseCase;

  @Override
  public ResponseEntity<GetUser200ResponseDTO> getUser(GetUserRequestDTO getUserRequestDTO) {
    val result = userRequestUseCase.findUser(
        exampleMapper.infra2domain(getUserRequestDTO));

    return ResponseEntity.ok(
        exampleMapper.domain2infra(result));
  }

}
