package io.github.flowrapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.Objects.requireNonNullElse;

/**
 * Global controller advice for handling exceptions across all controllers.
 * This class can be extended to add custom exception handling logic.
 * <p>
 * Note: should not be in application, but in infrastructure module. For practical purposes
 * it is placed here.
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(FunctionalException.class)
    public ProblemDetail handleFunctionalException(final FunctionalException functionalEx) {
        log.debug("Received functional exception with code: {}", functionalEx.getCode());

        return ProblemDetail.forStatusAndDetail(
                requireNonNullElse(HttpStatus.resolve(functionalEx.getStatus()), HttpStatus.I_AM_A_TEAPOT),
                functionalEx.getMessage());
    }

    /**
     * Generic exception handler for all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(final Exception ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }

}
