package io.github.flowrapp.infrastructure.input.rest.mainapi.config;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.flowrapp.exception.FunctionalException;

import jakarta.validation.ConstraintViolationException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class GlobalControllerAdviceTest {

  @InjectMocks
  private GlobalControllerAdvice globalControllerAdvice;

  @InstancioSource(samples = 1)
  @ParameterizedTest
  void handleFunctionalException(FunctionalException functionalException) {
    // GIVEN

    // WHEN
    final var problemDetail = globalControllerAdvice.handleFunctionalException(functionalException);

    // THEN
    assertNotNull(problemDetail);
  }

  @InstancioSource(samples = 1)
  @ParameterizedTest
  void handleAuthorizationDeniedException(AuthorizationDeniedException authorizationDeniedException) {
    // GIVEN

    // WHEN
    final var problemDetail = globalControllerAdvice.handleAuthorizationDeniedException(authorizationDeniedException);

    // THEN
    assertThat(problemDetail)
        .isNotNull()
        .returns(HttpStatus.FORBIDDEN.value(), ProblemDetail::getStatus);
  }

  @InstancioSource(samples = 1)
  @ParameterizedTest
  void handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
    // GIVEN

    // WHEN
    final var problemDetail = globalControllerAdvice.handleConstraintViolationException(constraintViolationException);

    // THEN
    assertThat(problemDetail)
        .isNotNull()
        .returns(HttpStatus.BAD_REQUEST.value(), ProblemDetail::getStatus);
  }

  @InstancioSource(samples = 1)
  @ParameterizedTest
  void handleGenericException(Exception exception) {
    // GIVEN

    // WHEN
    final var problemDetail = globalControllerAdvice.handleGenericException(exception);

    // THEN
    assertThat(problemDetail)
        .isNotNull()
        .returns(HttpStatus.INTERNAL_SERVER_ERROR.value(), ProblemDetail::getStatus)
        .extracting(ProblemDetail::getDetail)
        .isNotNull();
  }
}
