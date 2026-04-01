package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.fixtures.TestFixtures;
import com.briangabini.coffee_erp_backend.security.JwtAuthenticationFilter;
import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.security.SecurityConfig;
import com.briangabini.coffee_erp_backend.services.CoffeeBeanService;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.briangabini.coffee_erp_backend.fixtures.TestFixtures.buildBeanDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test Examples - https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html#authorizing-endpoints
@WebMvcTest(controllers = CoffeeBeanController.class)
@Import({SecurityConfig.class})
@DisplayName("Coffee Bean Security Tests")
public class CoffeeBeanControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CoffeeBeanService coffeeBeanService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    private final String API_URL = "/api/v1/coffee-beans";

    @Test
    @DisplayName("Unauthenticated users get 403 Forbidden")
    void unauthenticatedUser_Gets403() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "random.permission")
    @DisplayName("Users without correct authority get 403 Forbidden")
    void wrongAuthority_Gets403() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "coffee_bean.read")
    @DisplayName("Users with read authority can GET but cannot POST")
    void readAuthority_CanGet_CannotPost() throws Exception {

        String validJson = "{\"name\": \"Test Bean\", \"roastLevel\": \"MEDIUM\", \"origin\": \"Colombia\", \"pricePerKg\": 15.50}";

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk());

        mockMvc.perform(post(API_URL)
                        .contentType("application/json")
                        .content(validJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "coffee_bean.create")
    @DisplayName("Users with create authority can POST")
    void createAuthority_CanPost() throws Exception {

        String validJson = "{\"name\": \"Test Bean\", \"roastLevel\": \"MEDIUM\", \"origin\": \"Colombia\", \"pricePerKg\": 15.50}";

        CoffeeBeanDto mockSavedBean = buildBeanDto(UUID.randomUUID(), "Test Bean");

        given(coffeeBeanService.createBean(any())).willReturn(mockSavedBean);

        mockMvc.perform(post(API_URL)
                        .contentType("application/json")
                        .content(validJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN role WITHOUT explicit permission gets 403 (Proves strict PoLP)")
    void adminWithoutPermission_Gets403() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isForbidden());
    }
}