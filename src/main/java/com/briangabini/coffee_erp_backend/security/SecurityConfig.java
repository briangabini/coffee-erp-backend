package com.briangabini.coffee_erp_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-security-filters
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final UserDetailsService userDetailsService;

    @Bean
    // https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#disable-csrf
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)                                               // https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#disable-csrf
                .authorizeHttpRequests((authorize) -> authorize         // https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session           // https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#stateless-authentication
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);         // https://docs.spring.io/spring-security/reference/servlet/architecture.html#adding-custom-filter

        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);                       // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html#servlet-authentication-daoauthenticationprovider
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/password-encoder.html#page-title
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
