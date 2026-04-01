package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.web.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@DisplayName("Auth Controller Web Tests")
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthenticationManager authenticationManager;

    @MockitoBean
    UserDetailsService userDetailsService;

    @MockitoBean
    JwtService jwtService;

    private final String API_URL = "/api/v1/auth";

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class LoginEndpoints {

        @Test
        @DisplayName("Should return 200 OK and JWT Token when credentials are valid")
        void authenticate_ValidCredentials() throws Exception {

            // given
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password("admin123")
                    .build();

            UserDetails mockUserDetails = new User("admin", "admin123", Collections.emptyList());

            Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                    mockUserDetails,
                    null,
                    mockUserDetails.getAuthorities()
            );

            String expectedToken = "mock.jwt.token";

            given(authenticationManager.authenticate(any())).willReturn(mockAuth);
            given(jwtService.generateToken(mockUserDetails)).willReturn(expectedToken);

            String inputJson = objectMapper.writeValueAsString(request);

            // when / then
            mockMvc.perform(post(API_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(expectedToken));
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when credentials are invalid")
        void authenticate_InvalidCredentials() throws Exception {

            // given
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password("wrongpassword")
                    .build();

            given(authenticationManager.authenticate(any()))
                    .willThrow(new BadCredentialsException("Bad credentials"));

            String inputJson = objectMapper.writeValueAsString(request);

            // when / then
            mockMvc.perform(post(API_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login - Validation")
    class LoginValidationEndpoints {

        @Test
        @DisplayName("Should return 400 Bad Request when username is blank")
        void authenticate_UsernameIsBlank() throws Exception {

            // given
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("admin123")
                    .build();

            String inputJson = objectMapper.writeValueAsString(request);

            // when / then
            mockMvc.perform(post(API_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when password is blank")
        void authenticate_PasswordIsBlank() throws Exception {

            // given
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password("")
                    .build();

            String inputJson = objectMapper.writeValueAsString(request);

            // when / then
            mockMvc.perform(post(API_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isBadRequest());
        }
    }
}