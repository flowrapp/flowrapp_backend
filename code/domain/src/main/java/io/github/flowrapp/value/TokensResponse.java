package io.github.flowrapp.value;

import lombok.Builder;

/** Represents the response containing access and refresh tokens. */
@Builder(toBuilder = true)
public record TokensResponse(
    String accessToken,
    String refreshToken) {
}
