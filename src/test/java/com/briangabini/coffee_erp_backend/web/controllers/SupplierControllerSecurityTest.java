package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.JwtAuthenticationFilter;
import com.briangabini.coffee_erp_backend.security.JwtService;
import com.briangabini.coffee_erp_backend.security.SecurityConfig;
import com.briangabini.coffee_erp_backend.services.SupplierService;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
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

    @MockitoBean
    SupplierService supplierService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    private final String API_URL = "/api/v1/suppliers";

    @Test
    @DisplayName("Unauthenticated users get 403 Forbidden")
    void unauthenticatedUser_Gets403() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "supplier.read")
    @DisplayName("Users with read authority can GET but cannot POST")
    void readAuthority_CanGet_CannotPost() throws Exception {

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk());

        String validJson = "{\"name\": \"Global Beans Inc\", \"contactEmail\": \"sales@globalbeans.com\"}";
        mockMvc.perform(post(API_URL)
                        .contentType("application/json")
                        .content(validJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "supplier.create")
    @DisplayName("Users with create authority can POST")
    void createAuthority_CanPost() throws Exception {

        String validJson = "{\"name\": \"Global Beans Inc\", \"contactEmail\": \"sales@globalbeans.com\"}";

        SupplierDto mockSavedSupplier = buildSupplierDto(VALID_SUPPLIER_ID, VALID_SUPPLIER_NAME, VALID_SUPPLIER_EMAIL);
        given(supplierService.createSupplier(any())).willReturn(mockSavedSupplier);

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