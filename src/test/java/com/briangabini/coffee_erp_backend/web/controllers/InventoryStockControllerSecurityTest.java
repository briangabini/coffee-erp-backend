package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.JwtAuthenticationFilter;
import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.security.SecurityConfig;
import com.briangabini.coffee_erp_backend.services.InventoryStockService;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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

@WebMvcTest(controllers = InventoryStockController.class)
@Import({SecurityConfig.class})
@DisplayName("Inventory Stock Security Tests")
public class InventoryStockControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InventoryStockService inventoryStockService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String API_URL = "/api/v1/inventory";

    @Test
    @DisplayName("Unauthenticated users get 403 Forbidden")
    void unauthenticatedUser_Gets403() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "inventory_stock.read")
    @DisplayName("Users with read authority can GET but cannot POST")
    void readAuthority_CanGet_CannotPost() throws Exception {

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk());

        String validJson = "{\"coffeeBeanId\": 1, \"quantityInKg\": 50.0}";
        mockMvc.perform(post(API_URL)
                        .contentType("application/json")
                        .content(validJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(authorities = "inventory_stock.create")
    @DisplayName("Users with create authority can POST")
    void createAuthority_CanPost() throws Exception {


        InventoryStockDto mockSavedStock = buildStockDto(VALID_STOCK_ID, DEFAULT_QUANTITY, VALID_BEAN_ID);
        String validJson = objectMapper.writeValueAsString(mockSavedStock);
        given(inventoryStockService.addStock(any())).willReturn(mockSavedStock);

        mockMvc.perform(post(API_URL)
                        .contentType("application/json")
                        .content(validJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN role WITHOUT explicit permission gets 403")
    void adminWithoutPermission_Gets403() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isForbidden());
    }
}