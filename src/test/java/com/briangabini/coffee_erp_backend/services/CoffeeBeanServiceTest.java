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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.briangabini.coffee_erp_backend.fixtures.TestFixtures.*;
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

    @Nested
    @DisplayName("Get Bean By ID Tests")
    class GetBeanByIdTests {

        @Test
        @DisplayName("Should return mapped DTO when bean is found")
        void testGetBeanById_Found() {

            // given
            CoffeeBean mockEntity = buildBean(VALID_BEAN_ID, VALID_BEAN_NAME);
            CoffeeBeanDto mockDto = buildBeanDto(VALID_BEAN_ID, VALID_BEAN_NAME);

            given(coffeeBeanRepository.findById(VALID_BEAN_ID)).willReturn(Optional.of(mockEntity));
            given(coffeeBeanMapper.toCoffeeBeanDto(mockEntity)).willReturn(mockDto);

            // when
            CoffeeBeanDto result = coffeeBeanService.getBeanById(VALID_BEAN_ID);

            // then
            assertAll("Verify returned DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(VALID_BEAN_ID),
                    () -> assertThat(result.getName()).isEqualTo(VALID_BEAN_NAME)
            );

            verify(coffeeBeanRepository).findById(VALID_BEAN_ID);
            verify(coffeeBeanMapper).toCoffeeBeanDto(mockEntity);
        }

        @Test
        @DisplayName("Should throw exception when bean is NOT found")
        void testGetBeanById_NotFound() {

            // given
            given(coffeeBeanRepository.findById(VALID_BEAN_ID)).willReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class, () -> coffeeBeanService.getBeanById(VALID_BEAN_ID));

            verify(coffeeBeanRepository).findById(VALID_BEAN_ID);
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
            CoffeeBeanDto inputDto = buildBeanDto(null, NEW_BEAN_NAME);
            CoffeeBean mappedEntity = buildBean(null, NEW_BEAN_NAME);

            CoffeeBean savedEntity = buildBean(VALID_BEAN_ID, NEW_BEAN_NAME);
            CoffeeBeanDto outputDto = buildBeanDto(VALID_BEAN_ID, NEW_BEAN_NAME);

            given(coffeeBeanMapper.toCoffeeBean(inputDto)).willReturn(mappedEntity);
            given(coffeeBeanRepository.save(mappedEntity)).willReturn(savedEntity);
            given(coffeeBeanMapper.toCoffeeBeanDto(savedEntity)).willReturn(outputDto);

            // when
            CoffeeBeanDto result = coffeeBeanService.createBean(inputDto);

            // then
            assertAll("Verify created DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(VALID_BEAN_ID),
                    () -> assertThat(result.getName()).isEqualTo(NEW_BEAN_NAME)
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
            CoffeeBean entity1 = buildBean(UUID.randomUUID(), BEAN_1_NAME);
            CoffeeBean entity2 = buildBean(UUID.randomUUID(), BEAN_2_NAME);
            CoffeeBeanDto dto1 = buildBeanDto(entity1.getId(), BEAN_1_NAME);
            CoffeeBeanDto dto2 = buildBeanDto(entity2.getId(), BEAN_2_NAME);

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
}