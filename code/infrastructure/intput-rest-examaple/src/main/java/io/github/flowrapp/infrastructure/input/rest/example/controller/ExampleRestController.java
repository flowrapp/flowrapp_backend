package io.github.flowrapp.infrastructure.input.rest.example.controller;

import io.github.flowrapp.infrastructure.input.rest.example.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.example.dto.UserResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.example.mapper.ExampleMapper;
import io.github.flowrapp.port.input.UserRequestUseCase;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ExampleRestController {

  private final ExampleMapper exampleMapper;

  private final UserRequestUseCase userRequestUseCase;

  @GetMapping
  public UserResponseDTO getUser(@RequestBody UserRequestDTO userRequestDTO) {
    val result = userRequestUseCase.findUser(
        exampleMapper.infra2domain(userRequestDTO));

    return exampleMapper.domain2infra(result);
  }

}
