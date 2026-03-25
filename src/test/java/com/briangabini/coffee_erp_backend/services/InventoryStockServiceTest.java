package com.briangabini.coffee_erp_backend.services;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import com.briangabini.coffee_erp_backend.exceptions.ResourceNotFoundException;
import com.briangabini.coffee_erp_backend.repositories.CoffeeBeanRepository;
import com.briangabini.coffee_erp_backend.repositories.InventoryStockRepository;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import com.briangabini.coffee_erp_backend.web.mappers.InventoryStockMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Stock Service Unit Tests")
class InventoryStockServiceTest {

    @Mock
    InventoryStockRepository inventoryStockRepository;

    @Mock
    CoffeeBeanRepository coffeeBeanRepository;

    @Mock
    InventoryStockMapper inventoryStockMapper;

    @InjectMocks
    InventoryStockService inventoryStockService;

    private final UUID VALID_BEAN_ID = UUID.randomUUID();
    private final UUID VALID_STOCK_ID = UUID.randomUUID();
    private final int DEFAULT_QUANTITY = 500;

    @Nested
    @DisplayName("Add Stock Tests")
    class AddStockTests {

        @Test
        @DisplayName("Should fetch bean, map, attach, save, and return DTO (Happy Path)")
        void testAddStock_Success() {
            // given
            CoffeeBean existingBean = buildBean(VALID_BEAN_ID);
            InventoryStockDto inputDto = buildStockDto(null, DEFAULT_QUANTITY, VALID_BEAN_ID);
            InventoryStock unlinkedStock = buildStock(null, DEFAULT_QUANTITY, null);
            InventoryStock savedStock = buildStock(VALID_STOCK_ID, DEFAULT_QUANTITY, existingBean);
            InventoryStockDto outputDto = buildStockDto(VALID_STOCK_ID, DEFAULT_QUANTITY, VALID_BEAN_ID);

            given(coffeeBeanRepository.findById(VALID_BEAN_ID)).willReturn(Optional.of(existingBean));
            given(inventoryStockMapper.toInventoryStock(inputDto)).willReturn(unlinkedStock);
            given(inventoryStockRepository.save(unlinkedStock)).willReturn(savedStock);
            given(inventoryStockMapper.toInventoryStockDto(savedStock)).willReturn(outputDto);

            // when
            InventoryStockDto result = inventoryStockService.addStock(inputDto);

            // then
            assertAll("Verify returned stock properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(VALID_STOCK_ID),
                    () -> assertThat(result.getCoffeeBeanId()).isEqualTo(VALID_BEAN_ID),
                    () -> assertThat(result.getQuantityGrams()).isEqualTo(DEFAULT_QUANTITY)
            );

            verify(coffeeBeanRepository).findById(VALID_BEAN_ID);
            verify(inventoryStockMapper).toInventoryStock(inputDto);
            assertThat(unlinkedStock.getCoffeeBean()).isEqualTo(existingBean);
            verify(inventoryStockRepository).save(unlinkedStock);
            verify(inventoryStockMapper).toInventoryStockDto(savedStock);
        }

        @Test
        @DisplayName("Should abort and throw exception if Coffee Bean is not found (Sad Path)")
        void testAddStock_BeanNotFound() {

            // given
            InventoryStockDto inputDto = buildStockDto(null, DEFAULT_QUANTITY, VALID_BEAN_ID);

            given(coffeeBeanRepository.findById(VALID_BEAN_ID)).willReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class, () -> inventoryStockService.addStock(inputDto));

            verify(coffeeBeanRepository).findById(VALID_BEAN_ID);
            verifyNoInteractions(inventoryStockMapper);
            verifyNoInteractions(inventoryStockRepository);
        }
    }

    @Nested
    @DisplayName("Get All Stock Tests")
    class GetAllStockTests {

        @Test
        @DisplayName("Should return list of mapped DTOs")
        void testGetAllStock() {

            // given
            InventoryStock stock1 = buildStock(UUID.randomUUID(), 100, null);
            InventoryStock stock2 = buildStock(UUID.randomUUID(), 200, null);

            InventoryStockDto dto1 = buildStockDto(stock1.getId(), 100, VALID_BEAN_ID);
            InventoryStockDto dto2 = buildStockDto(stock2.getId(), 200, VALID_BEAN_ID);

            given(inventoryStockRepository.findAll()).willReturn(List.of(stock1, stock2));
            given(inventoryStockMapper.toInventoryStockDto(stock1)).willReturn(dto1);
            given(inventoryStockMapper.toInventoryStockDto(stock2)).willReturn(dto2);

            // when
            List<InventoryStockDto> results = inventoryStockService.getAllStock();

            // then
            assertAll("Verify list contents",
                    () -> assertThat(results).isNotNull(),
                    () -> assertThat(results).hasSize(2)
            );

            verify(inventoryStockRepository).findAll();
            verify(inventoryStockMapper).toInventoryStockDto(stock1);
            verify(inventoryStockMapper).toInventoryStockDto(stock2);
        }
    }

    private CoffeeBean buildBean(UUID id) {
        return CoffeeBean.builder()
                .id(id)
                .name("Target Bean")
                .build();
    }

    private InventoryStock buildStock(UUID id, int quantityGrams, CoffeeBean coffeeBean) {
        return InventoryStock.builder()
                .id(id)
                .quantityGrams(quantityGrams)
                .coffeeBean(coffeeBean)
                .build();
    }

    private InventoryStockDto buildStockDto(UUID id, int quantityGrams, UUID coffeeBeanId) {
        return InventoryStockDto.builder()
                .id(id)
                .coffeeBeanId(coffeeBeanId)
                .quantityGrams(quantityGrams)
                .build();
    }
}