package com.briangabini.coffee_erp_backend.services;

import com.briangabini.coffee_erp_backend.domain.Supplier;
import com.briangabini.coffee_erp_backend.exceptions.ResourceNotFoundException;
import com.briangabini.coffee_erp_backend.repositories.SupplierRepository;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
import com.briangabini.coffee_erp_backend.web.mappers.SupplierMapper;
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
@DisplayName("Supplier Service Unit Tests")
class SupplierServiceTest {

    @Mock
    SupplierRepository supplierRepository;

    @Mock
    SupplierMapper supplierMapper;

    @InjectMocks
    SupplierService supplierService;

    @Nested
    @DisplayName("Get Supplier By ID Tests")
    class GetSupplierByIdTests {

        @Test
        @DisplayName("Should return mapped DTO when supplier is found")
        void testGetSupplierById_Found() {

            // given
            UUID testId = UUID.randomUUID();

            Supplier mockEntity = Supplier.builder()
                    .id(testId)
                    .name("Global Bean Importers")
                    .contactEmail("hello@globalbeans.com")
                    .build();

            SupplierDto mockDto = SupplierDto.builder()
                    .id(testId)
                    .name("Global Bean Importers")
                    .contactEmail("hello@globalbeans.com")
                    .build();

            given(supplierRepository.findById(testId)).willReturn(Optional.of(mockEntity));
            given(supplierMapper.toSupplierDto(mockEntity)).willReturn(mockDto);

            // when
            SupplierDto result = supplierService.getSupplierById(testId);

            // then
            assertAll("Verify returned DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(testId),
                    () -> assertThat(result.getName()).isEqualTo("Global Bean Importers"),
                    () -> assertThat(result.getContactEmail()).isEqualTo("hello@globalbeans.com")
            );

            verify(supplierRepository).findById(testId);
            verify(supplierMapper).toSupplierDto(mockEntity);
        }

        @Test
        @DisplayName("Should throw exception when supplier is NOT found")
        void testGetSupplierById_NotFound() {

            // given
            UUID testId = UUID.randomUUID();
            given(supplierRepository.findById(testId)).willReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(testId));

            verify(supplierRepository).findById(testId);
            verifyNoInteractions(supplierMapper);
        }
    }

    @Nested
    @DisplayName("Create Supplier Tests")
    class CreateSupplierTests {

        @Test
        @DisplayName("Should map, save, and return DTO")
        void testCreateSupplier() {

            // given
            SupplierDto inputDto = SupplierDto.builder()
                    .name("New Supplier")
                    .contactEmail("sales@newsupplier.com")
                    .build();

            Supplier mappedEntity = Supplier.builder()
                    .name("New Supplier")
                    .contactEmail("sales@newsupplier.com")
                    .build();

            UUID savedId = UUID.randomUUID();

            Supplier savedEntity = Supplier.builder()
                    .id(savedId)
                    .name("New Supplier")
                    .contactEmail("sales@newsupplier.com")
                    .build();

            SupplierDto outputDto = SupplierDto.builder()
                    .id(savedId)
                    .name("New Supplier")
                    .contactEmail("sales@newsupplier.com")
                    .build();

            given(supplierMapper.toSupplier(inputDto)).willReturn(mappedEntity);
            given(supplierRepository.save(mappedEntity)).willReturn(savedEntity);
            given(supplierMapper.toSupplierDto(savedEntity)).willReturn(outputDto);

            // when
            SupplierDto result = supplierService.createSupplier(inputDto);

            // then
            assertAll("Verify created DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(savedId),
                    () -> assertThat(result.getName()).isEqualTo("New Supplier"),
                    () -> assertThat(result.getContactEmail()).isEqualTo("sales@newsupplier.com")
            );

            verify(supplierMapper).toSupplier(inputDto);
            verify(supplierRepository).save(mappedEntity);
            verify(supplierMapper).toSupplierDto(savedEntity);
        }
    }

    @Nested
    @DisplayName("Get All Suppliers Tests")
    class GetAllSuppliersTests {

        @Test
        @DisplayName("Should return list of mapped DTOs")
        void testGetAllSuppliers() {

            // given
            Supplier entity1 = Supplier.builder()
                    .name("Supplier 1")
                    .contactEmail("one@supplier.com")
                    .build();

            Supplier entity2 = Supplier.builder()
                    .name("Supplier 2")
                    .contactEmail("two@supplier.com")
                    .build();

            SupplierDto dto1 = SupplierDto.builder()
                    .name("Supplier 1")
                    .contactEmail("one@supplier.com")
                    .build();

            SupplierDto dto2 = SupplierDto.builder()
                    .name("Supplier 2")
                    .contactEmail("two@supplier.com")
                    .build();

            given(supplierRepository.findAll()).willReturn(List.of(entity1, entity2));
            given(supplierMapper.toSupplierDto(entity1)).willReturn(dto1);
            given(supplierMapper.toSupplierDto(entity2)).willReturn(dto2);

            // when
            List<SupplierDto> results = supplierService.getAllSuppliers();

            // then
            assertAll("Verify list contents",
                    () -> assertThat(results).isNotNull(),
                    () -> assertThat(results).hasSize(2)
            );

            verify(supplierRepository).findAll();
            verify(supplierMapper).toSupplierDto(entity1);
            verify(supplierMapper).toSupplierDto(entity2);
        }
    }
}