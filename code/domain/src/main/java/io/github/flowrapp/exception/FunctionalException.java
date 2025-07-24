package io.github.flowrapp.exception;

import java.io.Serial;

import lombok.Getter;

@Getter
public class FunctionalException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 5773333409155145909L;

  private final int code;

  private final int status;

  public FunctionalException(int code, int status, String message) {
    super(message);

    this.code = code;
    this.status = status;
  }

  public FunctionalException(int code, int status, String message, Throwable cause) {
    super(message, cause);

    this.code = code;
    this.status = status;
  }

  public FunctionalException(FunctionalError error) {
    this(error.getCode(), error.getStatus(), error.getMessage());
  }

  public FunctionalException(FunctionalError error, Throwable cause) {
    this(error.getCode(), error.getStatus(), error.getMessage(), cause);
  }

}
