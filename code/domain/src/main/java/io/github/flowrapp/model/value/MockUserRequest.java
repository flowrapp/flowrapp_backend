package io.github.flowrapp.model.value;

import lombok.Builder;

@Builder(toBuilder = true)
public record MockUserRequest(
    String name) {

}
