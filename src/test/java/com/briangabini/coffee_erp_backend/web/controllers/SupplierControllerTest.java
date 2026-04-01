package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.exceptions.ResourceNotFoundException;
import com.briangabini.coffee_erp_backend.security.JwtAuthenticationFilter;
import com.briangabini.coffee_erp_backend.services.SupplierService;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.briangabini.coffee_erp_backend.fixtures.TestFixtures.*;
import static com.briangabini.coffee_erp_backend.web.dto.ValidationMessages.EMAIL_INVALID;
import static com.briangabini.coffee_erp_backend.web.dto.ValidationMessages.SUPPLIER_NAME_REQUIRED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SupplierController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@DisplayName("Supplier Controller Web Tests")
class SupplierControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SupplierService supplierService;

    private final String API_URL = "/api/v1/suppliers";

    @Nested
    @DisplayName("GET /api/v1/suppliers/{id}")
    class GetSupplierByIdEndpoints {

        @Test
        @DisplayName("Should return 200 OK and Supplier JSON when found")
        void getSupplierById_Found() throws Exception {

            // given
            SupplierDto mockDto = buildSupplierDto(VALID_SUPPLIER_ID, VALID_SUPPLIER_NAME, VALID_SUPPLIER_EMAIL);
            given(supplierService.getSupplierById(VALID_SUPPLIER_ID)).willReturn(mockDto);

            // when / then
            mockMvc.perform(get(API_URL + "/" + VALID_SUPPLIER_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(VALID_SUPPLIER_ID.toString()))
                    .andExpect(jsonPath("$.name").value(VALID_SUPPLIER_NAME))
                    .andExpect(jsonPath("$.contactEmail").value(VALID_SUPPLIER_EMAIL));
        }

        @Test
        @DisplayName("Should return 404 Not Found when supplier does not exist")
        void getSupplierById_NotFound() throws Exception {
            // given
            given(supplierService.getSupplierById(VALID_SUPPLIER_ID))
                    .willThrow(new ResourceNotFoundException("Supplier not found"));

            // when / then
            mockMvc.perform(get(API_URL + "/" + VALID_SUPPLIER_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/suppliers")
    class GetAllSuppliersEndpoints {

        @Test
        @DisplayName("Should return 200 OK and List of Suppliers")
        void getAllSuppliers() throws Exception {

            // given
            SupplierDto dto1 = buildSupplierDto(VALID_SUPPLIER_ID, VALID_SUPPLIER_NAME, VALID_SUPPLIER_EMAIL);
            given(supplierService.getAllSuppliers()).willReturn(List.of(dto1));

            // when / then
            mockMvc.perform(get(API_URL)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(VALID_SUPPLIER_ID.toString()))
                    .andExpect(jsonPath("$[0].name").value(VALID_SUPPLIER_NAME))
                    .andExpect(jsonPath("$[0].contactEmail").value(VALID_SUPPLIER_EMAIL));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/suppliers")
    class CreateSupplierEndpoints {

        @Test
        @DisplayName("Should return 201 Created and Location Header")
        void createSupplier() throws Exception {
            // given
            SupplierDto inputDto = buildSupplierDto(null, NEW_SUPPLIER_NAME, NEW_SUPPLIER_EMAIL);
            SupplierDto outputDto = buildSupplierDto(VALID_SUPPLIER_ID, NEW_SUPPLIER_NAME, NEW_SUPPLIER_EMAIL);

            String inputJson = objectMapper.writeValueAsString(inputDto);

            given(supplierService.createSupplier(any(SupplierDto.class))).willReturn(outputDto);

            // when / then
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + API_URL + "/" + VALID_SUPPLIER_ID));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/suppliers - Validation")
    class CreateSupplierValidationEndpoints {

        @Test
        @DisplayName("Should return 400 Bad Request when supplier name is blank")
        void createSupplier_NameIsBlank() throws Exception {

            // given valid data EXCEPT for name
            SupplierDto invalidDto = buildSupplierDto(null, "", VALID_SUPPLIER_EMAIL);

            String inputJson = objectMapper.writeValueAsString(invalidDto);

            // when / then
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").value(SUPPLIER_NAME_REQUIRED));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when email is blank (@NotBlank)")
        void createSupplier_EmailIsBlank() throws Exception {

            // given valid data EXCEPT for a blank email
            SupplierDto invalidDto = buildSupplierDto(null, VALID_SUPPLIER_NAME, "");

            String inputJson = objectMapper.writeValueAsString(invalidDto);

            // when / then
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.contactEmail").exists());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when email is formatted incorrectly (@Email)")
        void createSupplier_EmailIsInvalid() throws Exception {

            // given valid data EXCEPT for a malformed email
            SupplierDto invalidDto = buildSupplierDto(null, VALID_SUPPLIER_NAME, "not-an-email-address");

            String inputJson = objectMapper.writeValueAsString(invalidDto);

            // when / then
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.contactEmail").value(EMAIL_INVALID));
        }
    }
}