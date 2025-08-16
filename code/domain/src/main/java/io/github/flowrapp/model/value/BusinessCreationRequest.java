package io.github.flowrapp.model.value;

import io.github.flowrapp.model.Location;

import lombok.Builder;

@Builder(toBuilder = true)
public record BusinessCreationRequest(
    String name,
    Location location) {
}
