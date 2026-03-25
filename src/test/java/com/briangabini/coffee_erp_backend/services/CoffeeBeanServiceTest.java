package com.briangabini.coffee_erp_backend.services;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.exceptions.ResourceNotFoundException;
import com.briangabini.coffee_erp_backend.repositories.CoffeeBeanRepository;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import com.briangabini.coffee_erp_backend.web.mappers.CoffeeBeanMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
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
@Tag("unit")
@DisplayName("Coffee Bean Service Unit Tests")
public class CoffeeBeanServiceTest {

    @Mock
    CoffeeBeanRepository coffeeBeanRepository;

    @Mock
    CoffeeBeanMapper coffeeBeanMapper;

    @InjectMocks
    CoffeeBeanService coffeeBeanService;

    private final UUID VALID_ID = UUID.randomUUID();
    private final BigDecimal DEFAULT_PRICE = new BigDecimal("10.00");

    @Nested
    @DisplayName("Get Bean By ID Tests")
    class GetBeanByIdTests {

        @Test
        @DisplayName("Should return mapped DTO when bean is found")
        void testGetBeanById_Found() {

            // given
            CoffeeBean mockEntity = buildBean(VALID_ID, "Test Bean");
            CoffeeBeanDto mockDto = buildDto(VALID_ID, "Test Bean");

            given(coffeeBeanRepository.findById(VALID_ID)).willReturn(Optional.of(mockEntity));
            given(coffeeBeanMapper.toCoffeeBeanDto(mockEntity)).willReturn(mockDto);

            // when
            CoffeeBeanDto result = coffeeBeanService.getBeanById(VALID_ID);

            // then
            assertAll("Verify returned DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(VALID_ID),
                    () -> assertThat(result.getName()).isEqualTo("Test Bean")
            );

            verify(coffeeBeanRepository).findById(VALID_ID);
            verify(coffeeBeanMapper).toCoffeeBeanDto(mockEntity);
        }

        @Test
        @DisplayName("Should throw exception when bean is NOT found")
        void testGetBeanById_NotFound() {

            // given
            given(coffeeBeanRepository.findById(VALID_ID)).willReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class, () -> coffeeBeanService.getBeanById(VALID_ID));

            verify(coffeeBeanRepository).findById(VALID_ID);
            verifyNoInteractions(coffeeBeanMapper);
        }
    }

    @Nested
    @DisplayName("Create Bean Tests")
    class CreateBeanTests {

        @Test
        @DisplayName("Should map, save, and return DTO")
        void testCreateBean() {

            // given
            CoffeeBeanDto inputDto = buildDto(null, "New Bean");
            CoffeeBean mappedEntity = buildBean(null, "New Bean");

            CoffeeBean savedEntity = buildBean(VALID_ID, "New Bean");
            CoffeeBeanDto outputDto = buildDto(VALID_ID, "New Bean");

            given(coffeeBeanMapper.toCoffeeBean(inputDto)).willReturn(mappedEntity);
            given(coffeeBeanRepository.save(mappedEntity)).willReturn(savedEntity);
            given(coffeeBeanMapper.toCoffeeBeanDto(savedEntity)).willReturn(outputDto);

            // when
            CoffeeBeanDto result = coffeeBeanService.createBean(inputDto);

            // then
            assertAll("Verify created DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo("New Bean")
            );

            verify(coffeeBeanMapper).toCoffeeBean(inputDto);
            verify(coffeeBeanRepository).save(mappedEntity);
            verify(coffeeBeanMapper).toCoffeeBeanDto(savedEntity);
        }
    }

    @Nested
    @DisplayName("Get All Bean Tests")
    class GetAllBeansTests {

        @Test
        @DisplayName("Should return list of mapped DTOs")
        void testGetAllBeans() {

            // given
            CoffeeBean entity1 = buildBean(UUID.randomUUID(), "Bean 1");
            CoffeeBean entity2 = buildBean(UUID.randomUUID(), "Bean 2");
            CoffeeBeanDto dto1 = buildDto(entity1.getId(), "Bean 1");
            CoffeeBeanDto dto2 = buildDto(entity2.getId(), "Bean 2");

            given(coffeeBeanRepository.findAll()).willReturn(List.of(entity1, entity2));
            given(coffeeBeanMapper.toCoffeeBeanDto(entity1)).willReturn(dto1);
            given(coffeeBeanMapper.toCoffeeBeanDto(entity2)).willReturn(dto2);

            // when
            List<CoffeeBeanDto> results = coffeeBeanService.getAllBeans();

            // then
            assertAll("Verify list contents",
                    () -> assertThat(results).isNotNull(),
                    () -> assertThat(results).hasSize(2)
            );

            verify(coffeeBeanRepository).findAll();
            verify(coffeeBeanMapper).toCoffeeBeanDto(entity1);
            verify(coffeeBeanMapper).toCoffeeBeanDto(entity2);
        }

        @Test
        @DisplayName("Should return empty list when no beans exist")
        void testGetAllBeans_Empty() {

            // given
            given(coffeeBeanRepository.findAll()).willReturn(Collections.emptyList());

            // when
            List<CoffeeBeanDto> results = coffeeBeanService.getAllBeans();

            // then
            assertThat(results).isEmpty();

            verify(coffeeBeanRepository).findAll();
            verifyNoInteractions(coffeeBeanMapper);
        }
    }

    private CoffeeBean buildBean(UUID id, String name) {
        return CoffeeBean.builder()
                .id(id)
                .name(name)
                .pricePerKg(DEFAULT_PRICE)
                .build();
    }

    private CoffeeBeanDto buildDto(UUID id, String name) {
        return CoffeeBeanDto.builder()
                .id(id)
                .name(name)
                .pricePerKg(DEFAULT_PRICE)
                .build();
    }
}