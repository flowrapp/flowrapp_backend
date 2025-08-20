package io.github.flowrapp.value;

import java.time.ZoneId;

import io.github.flowrapp.model.Location;

import lombok.Builder;

@Builder(toBuilder = true)
public record BusinessCreationRequest(
    String name,
    ZoneId zone,
    Location location) {
}
