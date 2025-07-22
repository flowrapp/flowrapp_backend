package io.github.flowrapp.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserRequest(
        String name
) {
}
