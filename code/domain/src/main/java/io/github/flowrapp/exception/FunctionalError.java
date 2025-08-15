package io.github.flowrapp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum with the error codes and messages.
 */
@Getter
@RequiredArgsConstructor
public enum FunctionalError {
  USER_NOT_FOUND(1000, 404, "user not found"),
  INVALID_CREDENTIALS(1001, 401, "invalid credentials"),
  INVALID_REFRESH_TOKEN(1002, 401, "invalid refresh token"),
  ;

  private final int code;

  private final int status;

  private final String message;

}
