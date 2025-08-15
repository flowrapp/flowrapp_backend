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
  ADMIN_USER_NOT_FOUND(1001, 500, "admin user not found"),
  USERNAME_ALREADY_EXISTS(1002, 409, "username already exists"),
  INVALID_CREDENTIALS(1003, 401, "invalid credentials"),
  INVALID_REFRESH_TOKEN(1004, 401, "invalid refresh token"),
  ;

  private final int code;

  private final int status;

  private final String message;

}
