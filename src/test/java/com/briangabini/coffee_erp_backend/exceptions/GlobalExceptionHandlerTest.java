package com.briangabini.coffee_erp_backend.exceptions;

import com.briangabini.coffee_erp_backend.web.dto.ApiErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
        ApiErrorResponse body = response.getBody();

        // then
        assertThat(body).isNotNull();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(body.getStatus()).isEqualTo(404),
                () -> assertThat(body.getError()).isEqualTo("Not Found"),
                () -> assertThat(body.getMessage()).isEqualTo(errorMessage),
                () -> assertThat(body.getTimestamp()).isNotNull(),
                () -> assertThat(body.getValidationErrors()).isNull()
        );
    }

    @Test
    @DisplayName("Should translate MethodArgumentNotValidException to 400 API Error Response with validation errors")
    void testHandleValidationExceptions() {

        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("loginRequest", "username", "Username cannot be blank");
        FieldError fieldError2 = new FieldError("loginRequest", "password", "Password cannot be blank");

        given(ex.getBindingResult()).willReturn(bindingResult);
        given(bindingResult.getAllErrors()).willReturn(List.of(fieldError1, fieldError2));

        // when
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);
        ApiErrorResponse body = response.getBody();

        // then
        assertThat(body).isNotNull();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(body.getStatus()).isEqualTo(400),
                () -> assertThat(body.getError()).isEqualTo("Bad Request"),
                () -> assertThat(body.getMessage()).isEqualTo("Validation failed"),
                () -> assertThat(body.getTimestamp()).isNotNull(),
                () -> assertThat(body.getValidationErrors()).isNotNull(),
                () -> assertThat(body.getValidationErrors()).containsEntry("username", "Username cannot be blank"),
                () -> assertThat(body.getValidationErrors()).containsEntry("password", "Password cannot be blank")
        );
    }

    @Test
    @DisplayName("Should translate BadCredentialsException to 401 API Error Response")
    void testHandleBadCredentialsException() {

        // given
        String originalMessage = "Bad credentials";
        BadCredentialsException ex = new BadCredentialsException(originalMessage);

        // when
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex);
        ApiErrorResponse body = response.getBody();

        // then
        assertThat(body).isNotNull();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(body.getStatus()).isEqualTo(401),
                () -> assertThat(body.getError()).isEqualTo("Unauthorized"),
                () -> assertThat(body.getMessage()).isEqualTo("Invalid username or password"),
                () -> assertThat(body.getTimestamp()).isNotNull(),
                () -> assertThat(body.getValidationErrors()).isNull()
        );
    }
}