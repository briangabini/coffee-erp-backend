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

    @Nested
    @DisplayName("Add Stock Tests")
    class AddStockTests {

        @Test
        @DisplayName("Should fetch bean, map, attach, save, and return DTO (Happy Path)")
        void testAddStock_Success() {

            // given
            UUID beanId = UUID.randomUUID();
            UUID stockId = UUID.randomUUID();

            InventoryStockDto inputDto = InventoryStockDto.builder()
                    .coffeeBeanId(beanId)
                    .quantityGrams(500)
                    .build();

            CoffeeBean existingBean = CoffeeBean.builder()
                    .id(beanId)
                    .name("Target Bean")
                    .build();

            InventoryStock unlinkedStock = InventoryStock.builder()
                    .quantityGrams(500)
                    .build();

            InventoryStock savedStock = InventoryStock.builder()
                    .id(stockId)
                    .coffeeBean(existingBean)
                    .quantityGrams(500)
                    .build();

            InventoryStockDto outputDto = InventoryStockDto.builder()
                    .id(stockId)
                    .coffeeBeanId(beanId)
                    .quantityGrams(500)
                    .build();

            given(coffeeBeanRepository.findById(beanId)).willReturn(Optional.of(existingBean));
            given(inventoryStockMapper.toInventoryStock(inputDto)).willReturn(unlinkedStock);
            given(inventoryStockRepository.save(unlinkedStock)).willReturn(savedStock);
            given(inventoryStockMapper.toInventoryStockDto(savedStock)).willReturn(outputDto);

            // when
            InventoryStockDto result = inventoryStockService.addStock(inputDto);

            // then
            assertAll("Verify returned stock properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(stockId),
                    () -> assertThat(result.getCoffeeBeanId()).isEqualTo(beanId),
                    () -> assertThat(result.getQuantityGrams()).isEqualTo(500)
            );

            verify(coffeeBeanRepository).findById(beanId);
            verify(inventoryStockMapper).toInventoryStock(inputDto);
            assertThat(unlinkedStock.getCoffeeBean()).isEqualTo(existingBean);
            verify(inventoryStockRepository).save(unlinkedStock);
            verify(inventoryStockMapper).toInventoryStockDto(savedStock);
        }

        @Test
        @DisplayName("Should abort and throw exception if Coffee Bean is not found (Sad Path)")
        void testAddStock_BeanNotFound() {

            // given
            UUID invalidBeanId = UUID.randomUUID();

            InventoryStockDto inputDto = InventoryStockDto.builder()
                    .coffeeBeanId(invalidBeanId)
                    .quantityGrams(500)
                    .build();

            given(coffeeBeanRepository.findById(invalidBeanId)).willReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class, () -> inventoryStockService.addStock(inputDto));

            verify(coffeeBeanRepository).findById(invalidBeanId);
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
            InventoryStock stock1 = InventoryStock.builder().quantityGrams(100).build();
            InventoryStock stock2 = InventoryStock.builder().quantityGrams(200).build();

            InventoryStockDto dto1 = InventoryStockDto.builder().quantityGrams(100).build();
            InventoryStockDto dto2 = InventoryStockDto.builder().quantityGrams(200).build();

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
}