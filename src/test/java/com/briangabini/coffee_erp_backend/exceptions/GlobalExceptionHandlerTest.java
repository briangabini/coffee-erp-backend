package com.briangabini.coffee_erp_backend.exceptions;

import com.briangabini.coffee_erp_backend.web.dto.ApiErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Global Exception Handler Unit Tests")
public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should translate ResourceNotFoundException to 404 API Error Response")
    void testHandleResourceNotFoundException() {

        // given
        String errorMessage = "Coffee Bean not found with id: 123";
        ResourceNotFoundException ex = new ResourceNotFoundException(errorMessage);

        // when
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().getStatus()).isEqualTo(404),
                () -> assertThat(response.getBody().getError()).isEqualTo("Not Found"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo(errorMessage),
                () -> assertThat(response.getBody().getTimestamp()).isNotNull()
        );
    }
}
