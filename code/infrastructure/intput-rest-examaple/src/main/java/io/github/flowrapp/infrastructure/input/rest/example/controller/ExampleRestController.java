package io.github.flowrapp.infrastructure.input.rest.example.controller;

import io.github.flowrapp.infrastructure.input.rest.example.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.example.dto.UserResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.example.mapper.ExampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExampleRestController {

    private final ExampleMapper exampleMapper;

    public UserResponseDTO getUser(@RequestBody UserRequestDTO userRequestDTO) {
        return null;
    }

}
