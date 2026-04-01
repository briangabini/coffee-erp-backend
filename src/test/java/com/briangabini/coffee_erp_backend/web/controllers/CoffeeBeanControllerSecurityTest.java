package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.security.SecurityConfig;
import com.briangabini.coffee_erp_backend.services.CoffeeBeanService;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.briangabini.coffee_erp_backend.fixtures.TestFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CoffeeBeanController.class)
@Import({SecurityConfig.class})
@DisplayName("Coffee Bean Security Tests")
public class CoffeeBeanControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CoffeeBeanService coffeeBeanService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    private final String API_URL = "/api/v1/coffee-beans";

    @Nested
    @DisplayName("GET " + API_URL)
    class GetCoffeeBeans {

        @Test
        @DisplayName("Unauthenticated users get 403 Forbidden")
        void unauthenticatedUser_Gets403() throws Exception {
            mockMvc.perform(get(API_URL))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "coffee_bean.read")
        @DisplayName("Users with read authority get 200 OK")
        void readAuthority_Gets200() throws Exception {
            mockMvc.perform(get(API_URL))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST " + API_URL)
    class CreateCoffeeBean {

        @Test
        @DisplayName("Unauthenticated users get 403 Forbidden")
        void unauthenticatedUser_Gets403() throws Exception {
            CoffeeBeanDto requestDto = buildBeanDto(null, VALID_BEAN_NAME);

            mockMvc.perform(post(API_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "coffee_bean.create")
        @DisplayName("Users with create authority get 201 Created")
        void createAuthority_Gets201() throws Exception {
            CoffeeBeanDto requestDto = buildBeanDto(null, VALID_BEAN_NAME);
            CoffeeBeanDto mockSavedBean = buildBeanDto(VALID_BEAN_ID, VALID_BEAN_NAME);

            given(coffeeBeanService.createBean(any())).willReturn(mockSavedBean);

            mockMvc.perform(post(API_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }
    }
}