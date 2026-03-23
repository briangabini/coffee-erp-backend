package com.briangabini.coffee_erp_backend.repositories;

import com.briangabini.coffee_erp_backend.domain.Supplier;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Testcontainers
@Tag("integration")
@DisplayName("Supplier Repository Integration Tests")
public class SupplierRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    SupplierRepository supplierRepository;


    @Test
    @DisplayName("Should successfully save a valid Supplier")
    void testSaveSupplier() {
        // given
        Supplier newSupplier = Supplier.builder()
                .name("Global Coffee Imports")
                .contactEmail("contact@globalcoffee.com")
                .build();

        // when
        Supplier savedSupplier = supplierRepository.save(newSupplier);
        supplierRepository.flush();

        // then
        assertAll("Verify Supplier properties and auditing fields",
                () -> assertThat(savedSupplier.getId()).isNotNull(),
                () -> assertThat(savedSupplier.getVersion()).isNotNull(),
                () -> assertThat(savedSupplier.getCreatedDate()).isNotNull(),
                () -> assertThat(savedSupplier.getName()).isEqualTo("Global Coffee Imports"),
                () -> assertThat(savedSupplier.getContactEmail()).isEqualTo("contact@globalcoffee.com")
        );
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when email format is invalid")
    void testSaveSupplierFailsValidationWhenEmailIsInvalid() {
        // given
        Supplier badSupplier = Supplier.builder()
                .name("Shady Beans Co.")
                .contactEmail("not-an-email-address") // Fails @Email
                .build();

        // when / then
        assertThrows(ConstraintViolationException.class, () -> {
            supplierRepository.save(badSupplier);
            supplierRepository.flush();
        });
    }
}
