package io.github.flowrapp.infrastructure.input.rest.users.controller;

import io.github.flowrapp.infrastructure.input.rest.users.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.users.dto.UserResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.users.mapper.ExampleMapper;
import io.github.flowrapp.port.input.UserRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
public class ExampleRestController {

  private final ExampleMapper exampleMapper;

  private final UserRequestUseCase userRequestUseCase;

  @Operation(summary = "Find a user by name", description = "Returns a user based on the provided name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = UserResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content)
  })
  @PostMapping
  public UserResponseDTO getUser(@RequestBody UserRequestDTO userRequestDTO) {
    val result = userRequestUseCase.findUser(
        exampleMapper.infra2domain(userRequestDTO));

    return exampleMapper.domain2infra(result);
  }

}
