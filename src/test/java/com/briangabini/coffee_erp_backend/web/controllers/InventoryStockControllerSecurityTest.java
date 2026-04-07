package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.security.SecurityConfig;
import com.briangabini.coffee_erp_backend.services.InventoryStockService;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
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

@WebMvcTest(controllers = InventoryStockController.class)
@Import({SecurityConfig.class})
@DisplayName("Inventory Stock Security Tests")
public class InventoryStockControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    InventoryStockService inventoryStockService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    private final String API_URL = "/api/v1/inventory";

    @Nested
    @DisplayName("GET " + API_URL)
    class GetInventoryStock {

        @Test
        @DisplayName("Unauthenticated users get 403 Forbidden")
        void unauthenticatedUser_Gets403() throws Exception {
            mockMvc.perform(get(API_URL))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "inventory_stock.read")
        @DisplayName("Users with read authority get 200 OK")
        void readAuthority_Gets200() throws Exception {
            mockMvc.perform(get(API_URL))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST " + API_URL)
    class CreateInventoryStock {

        @Test
        @DisplayName("Unauthenticated users get 403 Forbidden")
        void unauthenticatedUser_Gets403() throws Exception {
            InventoryStockDto requestDto = buildStockDto(null, DEFAULT_QUANTITY, VALID_BEAN_ID);

            mockMvc.perform(post(API_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "inventory_stock.create")
        @DisplayName("Users with create authority get 201 Created")
        void createAuthority_Gets201() throws Exception {
            InventoryStockDto requestDto = buildStockDto(null, DEFAULT_QUANTITY, VALID_BEAN_ID);
            InventoryStockDto mockSavedStock = buildStockDto(VALID_STOCK_ID, DEFAULT_QUANTITY, VALID_BEAN_ID);

            given(inventoryStockService.addStock(any())).willReturn(mockSavedStock);

            mockMvc.perform(post(API_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }
    }
}