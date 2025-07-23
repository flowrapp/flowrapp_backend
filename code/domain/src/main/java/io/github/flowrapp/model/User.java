package io.github.flowrapp.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record User(
    Long id,
    String name,
    String dni
) {

}
