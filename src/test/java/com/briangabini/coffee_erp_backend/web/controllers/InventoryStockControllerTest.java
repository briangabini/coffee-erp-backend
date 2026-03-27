package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.services.InventoryStockService;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.briangabini.coffee_erp_backend.fixtures.TestFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = InventoryStockController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@DisplayName("Inventory Stock Controller Web Tests")
class InventoryStockControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    InventoryStockService inventoryStockService;

    private final String API_URL = "/api/v1/inventory";

    @Nested
    @DisplayName("GET /api/v1/inventory")
    class GetAllStockEndpoints {

        @Test
        @DisplayName("Should return 200 OK and List of Stock")
        void getAllStock() throws Exception {

            // given
            InventoryStockDto dto1 = buildStockDto(VALID_STOCK_ID, DEFAULT_QUANTITY, VALID_BEAN_ID);
            given(inventoryStockService.getAllStock()).willReturn(List.of(dto1));

            // when / then
            mockMvc.perform(get(API_URL)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(VALID_STOCK_ID.toString()))
                    .andExpect(jsonPath("$[0].coffeeBeanId").value(VALID_BEAN_ID.toString()))
                    .andExpect(jsonPath("$[0].quantityGrams").value(DEFAULT_QUANTITY));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/inventory")
    class AddStockEndpoints {

        @Test
        @DisplayName("Should return 201 Created and Location Header")
        void addStock() throws Exception {

            // given
            InventoryStockDto inputDto = buildStockDto(null, DEFAULT_QUANTITY, VALID_BEAN_ID);
            InventoryStockDto outputDto = buildStockDto(VALID_STOCK_ID, DEFAULT_QUANTITY, VALID_BEAN_ID);

            String inputJson = objectMapper.writeValueAsString(inputDto);

            given(inventoryStockService.addStock(any(InventoryStockDto.class))).willReturn(outputDto);

            // when / then
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + API_URL + "/" + VALID_STOCK_ID));
        }
    }
}