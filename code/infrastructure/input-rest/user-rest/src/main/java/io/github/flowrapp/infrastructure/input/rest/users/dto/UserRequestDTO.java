package io.github.flowrapp.infrastructure.input.rest.users.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserRequestDTO(
    String name
) {

}
