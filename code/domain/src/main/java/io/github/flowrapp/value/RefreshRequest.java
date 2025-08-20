package io.github.flowrapp.value;

import lombok.Builder;

/** Represents a request to refresh authentication tokens. */
@Builder(toBuilder = true)
public record RefreshRequest(
    String refreshToken) {

  @Override
  public String toString() {
    return "RefreshRequest{"
        +
        "refreshToken='" + refreshToken.substring(0, 4) + "..." + '\'' +
        '}';
  }
}
