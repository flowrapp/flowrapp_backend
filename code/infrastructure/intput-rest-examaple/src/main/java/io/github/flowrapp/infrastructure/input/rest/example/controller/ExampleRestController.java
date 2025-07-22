package io.github.flowrapp.infrastructure.input.rest.example.controller;

import io.github.flowrapp.infrastructure.input.rest.example.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.example.dto.UserResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.example.mapper.ExampleMapper;
import io.github.flowrapp.port.input.UserRequestUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExampleRestController {

    private final ExampleMapper exampleMapper;

    private final UserRequestUseCase userRequestUseCase;

    public UserResponseDTO getUser(@RequestBody UserRequestDTO userRequestDTO) {
        final var result = this.userRequestUseCase.findUser(
                exampleMapper.infra2domain(userRequestDTO));

        return exampleMapper.domain2infra(result);
    }

}
