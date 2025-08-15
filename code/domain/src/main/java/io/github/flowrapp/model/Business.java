package io.github.flowrapp.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.github.flowrapp.model.value.Location;
import lombok.Builder;

@Builder(toBuilder = true)
public record Business(
    long id,
    String name,
    User owner,
    Location location,
    OffsetDateTime createdAt,
    List<User> members
) {
}
