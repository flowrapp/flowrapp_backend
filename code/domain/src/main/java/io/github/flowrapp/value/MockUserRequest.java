package io.github.flowrapp.value;

import lombok.Builder;

@Builder(toBuilder = true)
public record MockUserRequest(
    String name) {

}
