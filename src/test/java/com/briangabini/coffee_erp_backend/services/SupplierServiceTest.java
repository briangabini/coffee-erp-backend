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

    private final UUID VALID_ID = UUID.randomUUID();
    private final String VALID_NAME = "Global Bean Importers";
    private final String VALID_EMAIL = "hello@globalbeans.com";
    private final String NEW_NAME = "New Supplier";
    private final String NEW_EMAIL = "sales@newsupplier.com";

    @Nested
    @DisplayName("Get Supplier By ID Tests")
    class GetSupplierByIdTests {

        @Test
        @DisplayName("Should return mapped DTO when supplier is found")
        void testGetSupplierById_Found() {

            // given
            Supplier mockEntity = buildSupplier(VALID_ID, VALID_NAME, VALID_EMAIL);
            SupplierDto mockDto = buildSupplierDto(VALID_ID, VALID_NAME, VALID_EMAIL);

            given(supplierRepository.findById(VALID_ID)).willReturn(Optional.of(mockEntity));
            given(supplierMapper.toSupplierDto(mockEntity)).willReturn(mockDto);

            // when
            SupplierDto result = supplierService.getSupplierById(VALID_ID);

            // then
            assertAll("Verify returned DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(VALID_ID),
                    () -> assertThat(result.getName()).isEqualTo(VALID_NAME),
                    () -> assertThat(result.getContactEmail()).isEqualTo(VALID_EMAIL)
            );

            verify(supplierRepository).findById(VALID_ID);
            verify(supplierMapper).toSupplierDto(mockEntity);
        }

        @Test
        @DisplayName("Should throw exception when supplier is NOT found")
        void testGetSupplierById_NotFound() {

            // given
            given(supplierRepository.findById(VALID_ID)).willReturn(Optional.empty());

            // when / then
            assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(VALID_ID));

            verify(supplierRepository).findById(VALID_ID);
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
            SupplierDto inputDto = buildSupplierDto(null, NEW_NAME, NEW_EMAIL);
            Supplier mappedEntity = buildSupplier(null, NEW_NAME, NEW_EMAIL);
            Supplier savedEntity = buildSupplier(VALID_ID, NEW_NAME, NEW_EMAIL);
            SupplierDto outputDto = buildSupplierDto(VALID_ID, NEW_NAME, NEW_EMAIL);

            given(supplierMapper.toSupplier(inputDto)).willReturn(mappedEntity);
            given(supplierRepository.save(mappedEntity)).willReturn(savedEntity);
            given(supplierMapper.toSupplierDto(savedEntity)).willReturn(outputDto);

            // when
            SupplierDto result = supplierService.createSupplier(inputDto);

            // then
            assertAll("Verify created DTO properties",
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(VALID_ID),
                    () -> assertThat(result.getName()).isEqualTo(NEW_NAME),
                    () -> assertThat(result.getContactEmail()).isEqualTo(NEW_EMAIL)
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
            Supplier entity1 = buildSupplier(UUID.randomUUID(), VALID_NAME, VALID_EMAIL);
            Supplier entity2 = buildSupplier(UUID.randomUUID(), NEW_NAME, NEW_EMAIL);

            SupplierDto dto1 = buildSupplierDto(entity1.getId(), VALID_NAME, VALID_EMAIL);
            SupplierDto dto2 = buildSupplierDto(entity2.getId(), NEW_NAME, NEW_EMAIL);

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

    private Supplier buildSupplier(UUID id, String name, String contactEmail) {
        return Supplier.builder()
                .id(id)
                .name(name)
                .contactEmail(contactEmail)
                .build();
    }

    private SupplierDto buildSupplierDto(UUID id, String name, String contactEmail) {
        return SupplierDto.builder()
                .id(id)
                .name(name)
                .contactEmail(contactEmail)
                .build();
    }
}