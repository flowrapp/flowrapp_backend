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
  ;

  private final int code;

  private final int status;

  private final String message;

}
