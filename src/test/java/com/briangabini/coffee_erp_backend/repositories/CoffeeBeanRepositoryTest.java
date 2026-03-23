package com.briangabini.coffee_erp_backend.repositories;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import com.briangabini.coffee_erp_backend.domain.Supplier;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// https://java.testcontainers.org/test_framework_integration/junit_5/
@DataJpaTest
@Testcontainers
@Tag("integration")
@DisplayName("Coffee Bean Repository Integration Tests")
class CoffeeBeanRepositoryTest {

    @Container
    @ServiceConnection      // https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    CoffeeBeanRepository coffeeBeanRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Nested
    @DisplayName("Happy Path Operations & Relationships")
    class HappyPathTests{

        @Test
        void testSaveCoffeeBean() {
            // given
            CoffeeBean newBean = CoffeeBean.builder()
                    .name("Ethiopian Yirgacheffe")
                    .origin("Ethiopia")
                    .roastLevel("Light")
                    .pricePerKg(new BigDecimal("25.60"))
                    .build();

            // when
            CoffeeBean savedBean = coffeeBeanRepository.save(newBean);
            coffeeBeanRepository.flush();

            // then
            assertAll("Verify Coffee Bean was saved",
                    () -> assertThat(savedBean).isNotNull(),
                    () -> assertThat(savedBean.getId()).isNotNull(),
                    () -> assertThat(savedBean.getVersion()).isNotNull(),
                    () -> assertThat(savedBean.getCreatedDate()).isNotNull(),
                    () -> assertThat(savedBean.getLastModifiedDate()).isNotNull()
            );
        }

        @Test
        @DisplayName("Should cascade save to InventoryStock when CoffeeBean saved")
        void testSaveCoffeeBeanWithInventoryStock() {
            // given
            CoffeeBean newBean = CoffeeBean.builder()
                    .name("Sumatra Mandheling")
                    .origin("Indonesia")
                    .roastLevel("Dark")
                    .pricePerKg(new BigDecimal("18.50"))
                    .build();

            InventoryStock newStock = InventoryStock.builder()
                    .quantityGrams(10000)
                    .expiryDate(LocalDate.now().plusMonths(6))
                    .build();
            newBean.addInventoryStock(newStock);

            // when
            CoffeeBean savedBean = coffeeBeanRepository.save(newBean);
            coffeeBeanRepository.flush();

            // then
            assertThat(savedBean.getInventoryStocks()).hasSize(1);
            InventoryStock savedStock = savedBean.getInventoryStocks().iterator().next();

            assertAll("Verify child stock was saved via cascade",
                    () -> assertThat(savedStock.getId()).isNotNull(),
                    () -> assertThat(savedStock.getCreatedDate()).isNotNull(),
                    () -> assertThat(savedStock.getCoffeeBean().getId()).isEqualTo(savedBean.getId())
            );
        }

        @Test
        @DisplayName("Should successfully link CoffeeBean to an existing Supplier")
        void testSaveCoffeeBeanWithSupplier() {
            // given - Must save supplier first because there is no cascade on this relationship
            Supplier savedSupplier = supplierRepository.save(Supplier.builder()
                    .name("Global Coffee Imports")
                    .contactEmail("contact@globalcoffee.com")
                    .build());
            supplierRepository.flush();

            CoffeeBean newBean = CoffeeBean.builder()
                    .name("Colombian Supremo")
                    .origin("Colombia")
                    .roastLevel("Medium")
                    .pricePerKg(new BigDecimal("21.00"))
                    .build();
            newBean.addSupplier(savedSupplier);

            // when
            CoffeeBean savedBean = coffeeBeanRepository.save(newBean);
            coffeeBeanRepository.flush();

            // then
            assertThat(savedBean.getSuppliers()).hasSize(1);
            Supplier linkedSupplier = savedBean.getSuppliers().iterator().next();
            assertThat(linkedSupplier.getId()).isEqualTo(savedSupplier.getId());
        }
    }


    @Nested
    @DisplayName("Validation Boundary Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw ConstraintViolationException when pricePerKg is negative")
        void testSaveCoffeeBeanFailsValidationWhenPriceIsNegative() {
            // given
            CoffeeBean badBean = CoffeeBean.builder()
                    .name("Bad Bean")
                    .origin("Unknown")
                    .roastLevel("Dark")
                    .pricePerKg(new BigDecimal("-5.00"))
                    .build();

            // when / then
            // https://docs.hibernate.org/validator/8.0/reference/en-US/html_single/#section-validating-executable-constraints
            assertThrows(ConstraintViolationException.class, () -> {
                coffeeBeanRepository.save(badBean);
                coffeeBeanRepository.flush();
            });
        }
    }

}