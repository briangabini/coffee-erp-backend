package com.briangabini.coffee_erp_backend.controllers;

import com.briangabini.coffee_erp_backend.exceptions.ResourceNotFoundException;
import com.briangabini.coffee_erp_backend.services.CoffeeBeanService;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
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
        controllers = CoffeeBeanController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@DisplayName("Coffee Bean Controller Web Tests")
public class CoffeeBeanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectmapper;

    @MockitoBean
    CoffeeBeanService coffeeBeanService;

    private final String API_URL = "/api/v1/coffee-beans";

    @Nested
    @DisplayName("GET /api/v1/coffee-beans/{id}")
    class GetBeanByIdEndpoints {

        @Test
        @DisplayName("Should return 200 OK and Bean JSON when found")
        void getBeanById_Found() throws Exception {
            // given
            CoffeeBeanDto mockDto = buildBeanDto(VALID_BEAN_ID, VALID_BEAN_NAME);
            given(coffeeBeanService.getBeanById(VALID_BEAN_ID)).willReturn(mockDto);

            // when / then
            mockMvc.perform(get(API_URL + "/" + VALID_BEAN_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(VALID_BEAN_ID.toString()))
                    .andExpect(jsonPath("$.name").value(VALID_BEAN_NAME))
                    .andExpect(jsonPath("$.pricePerKg").value(DEFAULT_PRICE.toString()));
        }

        @Test
        @DisplayName("Should return 404 Not Found when bean does not exist")
        void getBeanById_NotFound() throws Exception {

            // given
            given(coffeeBeanService.getBeanById(VALID_BEAN_ID))
                    .willThrow(ResourceNotFoundException.class);

            // when / then
            mockMvc.perform(get(API_URL + "/" + VALID_BEAN_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/coffee-beans")
    class GetAllBeansEndpoints {

        @Test
        @DisplayName("Should return 200 OK and List of Beans")
        void getAllBeans() throws Exception {

            // given
            CoffeeBeanDto dto1 = buildBeanDto(VALID_BEAN_ID, BEAN_1_NAME);
            given(coffeeBeanService.getAllBeans()).willReturn(List.of(dto1));

            // when / then
            mockMvc.perform(get(API_URL)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(VALID_BEAN_ID.toString()))
                    .andExpect(jsonPath("$[0].name").value(BEAN_1_NAME));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/coffee-beans")
    class CreateBeanEndpoints {

        @Test
        @DisplayName("Should return 201 Created and Location Header")
        void createBean() throws Exception {

            // given
            CoffeeBeanDto inputDto = buildBeanDto(null, NEW_BEAN_NAME);
            CoffeeBeanDto outputDto = buildBeanDto(VALID_BEAN_ID, NEW_BEAN_NAME);

            String inputJson = objectmapper.writeValueAsString(inputDto);

            given(coffeeBeanService.createBean(any(CoffeeBeanDto.class))).willReturn(outputDto);

            // when / then
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + API_URL + "/" + VALID_BEAN_ID));
        }
    }
}
