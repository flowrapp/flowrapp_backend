package io.github.flowrapp.exception;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class GlobalControllerAdviceTest {

    @InjectMocks
    private GlobalControllerAdvice globalControllerAdvice;

    @InstancioSource
    @ParameterizedTest
    void handleFunctionalException(FunctionalException functionalException) {
        // GIVEN

        // WHEN
        final var problemDetail = globalControllerAdvice.handleFunctionalException(functionalException);

        // THEN
        assertNotNull(problemDetail);
    }

    @InstancioSource
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