package com.briangabini.coffee_erp_backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails dummyUser;

    @BeforeEach
    void setup() {

        jwtService = new JwtService();

        // https://www.baeldung.com/spring-reflection-test-utils
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60);                                                      // 1 hour

        dummyUser = new User("admin", "password", Collections.emptyList());
    }

    @Test
    void shouldGeneratedAndValidateToken() {

        // given
        String token = jwtService.generateToken(dummyUser);

        // when
        boolean isValid = jwtService.isTokenValid(token, dummyUser);

        // then
        assertThat(token).isNotBlank();
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldExtractUsernameCorrectly() {

        // given
        String token = jwtService.generateToken(dummyUser);

        // when
        String username = jwtService.extractUsername(token);

        // then
        assertThat(username).isEqualTo(dummyUser.getUsername());
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {

        // given
        String token = jwtService.generateToken(dummyUser);
        UserDetails wrongUser = new User("hacker", "password", Collections.emptyList());

        // when
        boolean isValid = jwtService.isTokenValid(token, wrongUser);

        // then
        assertThat(isValid).isFalse();
    }
}
