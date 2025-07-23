package io.github.flowrapp.infrastructure.input.rest.users.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserResponseDTO(
    String name,
    String dni
) {

}
