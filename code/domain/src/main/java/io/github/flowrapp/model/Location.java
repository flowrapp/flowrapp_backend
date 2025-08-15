package io.github.flowrapp.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Location(
    double latitude,
    double longitude,
    double area) {
}
