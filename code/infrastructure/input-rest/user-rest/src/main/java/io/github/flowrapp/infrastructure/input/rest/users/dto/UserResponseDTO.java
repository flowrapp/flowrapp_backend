package io.github.flowrapp.infrastructure.input.rest.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Response object containing user information")
public record UserResponseDTO(
    @Schema(description = "Name of the user", example = "John Doe") String name,

    @Schema(description = "Document National Identity of the user", example = "12345678A") String dni) {

}
