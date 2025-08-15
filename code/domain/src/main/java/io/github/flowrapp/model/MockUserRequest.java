package io.github.flowrapp.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record MockUserRequest(
    String name) {

}
