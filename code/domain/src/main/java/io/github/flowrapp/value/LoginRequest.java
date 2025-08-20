package io.github.flowrapp.value;

import lombok.Builder;

/** Represents a request for user login containing username and password. */
@Builder(toBuilder = true)
public record LoginRequest(
    String username,
    String password) {

  @Override
  public String toString() {
    return "LoginRequest{"
        +
        "username='" + username + '\'' +
        ", password='" + password.substring(0, 4) + "..." + '\'' +
        '}';
  }

}
