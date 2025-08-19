package io.github.flowrapp.model.value;

import java.time.ZoneId;

import io.github.flowrapp.model.Location;

import lombok.Builder;

@Builder(toBuilder = true)
public record BusinessCreationRequest(
    String name,
    ZoneId timezoneOffset,
    Location location) {
}
