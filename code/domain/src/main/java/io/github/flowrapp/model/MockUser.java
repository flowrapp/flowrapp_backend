package io.github.flowrapp.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record MockUser(
    Long id,
    String name,
    String dni) {

}
