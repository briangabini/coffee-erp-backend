package com.briangabini.coffee_erp_backend.repositories;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Testcontainers
@Tag("integration")
@DisplayName("Inventory Stock Repository Integration Tests")
public class InventoryStockRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    InventoryStockRepository inventoryStockRepository;

    @Autowired
    CoffeeBeanRepository coffeeBeanRepository;

    private CoffeeBean testBean;

    @BeforeEach
    void setup() {
        // We need to have a saved CoffeeBean to reference before we try to save an InventoryStock
        testBean = CoffeeBean.builder()
                .name("Test Bean")
                .origin("Test Origin")
                .roastLevel("Medium")
                .pricePerKg(new BigDecimal("15.00"))
                .build();
        coffeeBeanRepository.save(testBean);
        coffeeBeanRepository.flush();
    }

    @Nested
    @DisplayName("Happy Path Operations")
    class HappyPathTests {

        @Test
        @DisplayName("Should successfully save valid InventoryStock linked to a CoffeeBean")
        void testSaveInventoryStock() {
            // given
            InventoryStock newStock = InventoryStock.builder()
                    .coffeeBean(testBean)
                    .quantityGrams(5000)
                    .expiryDate(LocalDate.now().plusMonths(6))
                    .build();

            // when
            InventoryStock savedStock = inventoryStockRepository.save(newStock);
            inventoryStockRepository.flush();

            // then
            assertAll("Verify InventoryStock relationship and properties",
                    () -> assertThat(savedStock.getId()).isNotNull(),
                    () -> assertThat(savedStock.getCoffeeBean().getId()).isEqualTo(testBean.getId()),
                    () -> assertThat(savedStock.getQuantityGrams()).isEqualTo(5000)
            );
        }
    }

    @Nested
    @DisplayName("Validation boundary tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw ConstraintViolationException when quantity is negative")
        void testSaveInventoryStockFailsWhenQuantityNegative() {
            // given
            InventoryStock badStock = InventoryStock.builder()
                    .coffeeBean(testBean)
                    .quantityGrams(-100) // Fails @Min(0)
                    .build();

            // when / then
            assertThrows(ConstraintViolationException.class, () -> {
                inventoryStockRepository.save(badStock);
                inventoryStockRepository.flush();
            });
        }
    }
}
