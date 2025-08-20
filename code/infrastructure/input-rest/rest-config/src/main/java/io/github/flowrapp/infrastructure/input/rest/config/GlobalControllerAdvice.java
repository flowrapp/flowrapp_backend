package io.github.flowrapp.infrastructure.input.rest.config;

import static java.util.Objects.requireNonNullElse;

import io.github.flowrapp.exception.FunctionalException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global controller advice for handling exceptions across all controllers. This class can be extended to add custom exception handling
 * logic. Note: should not be in application, but in infrastructure module. For practical purposes it is placed here.
 */
@Slf4j
@RestControllerAdvice(basePackages = "io.github.flowrapp.infrastructure.input.rest")
public class GlobalControllerAdvice {

  @ExceptionHandler(FunctionalException.class)
  public ProblemDetail handleFunctionalException(final FunctionalException functionalEx) {
    log.warn("Received functional exception: {}", functionalEx.getMessage());

    return ProblemDetail.forStatusAndDetail(
        requireNonNullElse(HttpStatus.resolve(functionalEx.getStatus()), HttpStatus.I_AM_A_TEAPOT),
        functionalEx.getCode() + " - " + functionalEx.getMessage());
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ProblemDetail handleAuthorizationDeniedException(final AuthorizationDeniedException authEx) {
    log.warn("Authorization denied: {}", authEx.getMessage());

    return ProblemDetail.forStatusAndDetail(
        HttpStatus.FORBIDDEN, "You do not have permission to access this resource");
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolationException(final ConstraintViolationException ex) {
    log.warn("Constraint violation: {}", ex.getMessage());

    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Invalid request parameters: " + ex.getMessage());
  }

  /**
   * Generic exception handler for all other exceptions.
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(final Exception ex) {
    log.error("Unhandled exception", ex);
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
  }

}
