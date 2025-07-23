package io.github.flowrapp.infrastructure.input.rest.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Request object for user operations")
public record UserRequestDTO(
    @Schema(description = "Name of the user", example = "John Doe", required = true)
    String name
) {

}
