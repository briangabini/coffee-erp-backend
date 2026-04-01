package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.security.SecurityConfig;
import com.briangabini.coffee_erp_backend.services.SupplierService;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
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

@WebMvcTest(controllers = SupplierController.class)
@Import({SecurityConfig.class})
@DisplayName("Supplier Security Tests")
public class SupplierControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SupplierService supplierService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    private final String API_URL = "/api/v1/suppliers";

    @Nested
    @DisplayName("GET " + API_URL)
    class GetSuppliers {

        @Test
        @DisplayName("Unauthenticated users get 403 Forbidden")
        void unauthenticatedUser_Gets403() throws Exception {
            mockMvc.perform(get(API_URL))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "supplier.read")
        @DisplayName("Users with read authority get 200 OK")
        void readAuthority_Gets200() throws Exception {
            mockMvc.perform(get(API_URL))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST " + API_URL)
    class CreateSupplier {

        @Test
        @DisplayName("Unauthenticated users get 403 Forbidden")
        void unauthenticatedUser_Gets403() throws Exception {
            SupplierDto requestDto = buildSupplierDto(null, VALID_SUPPLIER_NAME, VALID_SUPPLIER_EMAIL);

            mockMvc.perform(post(API_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "supplier.create")
        @DisplayName("Users with create authority get 201 Created")
        void createAuthority_Gets201() throws Exception {
            SupplierDto requestDto = buildSupplierDto(null, VALID_SUPPLIER_NAME, VALID_SUPPLIER_EMAIL);
            SupplierDto mockSavedSupplier = buildSupplierDto(VALID_SUPPLIER_ID, VALID_SUPPLIER_NAME, VALID_SUPPLIER_EMAIL);

            given(supplierService.createSupplier(any())).willReturn(mockSavedSupplier);

            mockMvc.perform(post(API_URL)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }
    }
}