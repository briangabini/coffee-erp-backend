package com.briangabini.coffee_erp_backend.fixtures;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import com.briangabini.coffee_erp_backend.domain.Supplier;
import com.briangabini.coffee_erp_backend.domain.enums.RoastLevel;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestFixtures {

    // --- Identifiers & Numbers ---
    public static final UUID VALID_BEAN_ID = UUID.randomUUID();
    public static final UUID VALID_STOCK_ID = UUID.randomUUID();
    public static final UUID VALID_SUPPLIER_ID = UUID.randomUUID();

    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("10.0");
    public static final int DEFAULT_QUANTITY = 500;

    // --- String Constants ---
    public static final String VALID_BEAN_NAME = "Test Bean";
    public static final String NEW_BEAN_NAME = "New Bean";
    public static final String BEAN_1_NAME = "Ethiopian Yirgacheffe";
    public static final String BEAN_2_NAME = "Colombian Supremo";
    public static final String VALID_ORIGIN = "Colombia";
    public static final RoastLevel VALID_ROAST_LEVEL = RoastLevel.MEDIUM;

    public static final String VALID_SUPPLIER_NAME = "Global Bean Importers";
    public static final String VALID_SUPPLIER_EMAIL = "hello@globalbeans.com";
    public static final String NEW_SUPPLIER_NAME = "New Supplier";
    public static final String NEW_SUPPLIER_EMAIL = "sales@newsupplier.com";

    private TestFixtures() {
        throw new UnsupportedOperationException("Utility class");
    }

    // --- Coffee Bean Fixtures ---
    public static CoffeeBean buildBean(UUID id, String name) {
        return CoffeeBean.builder()
                .id(id)
                .name(name)
                .pricePerKg(DEFAULT_PRICE)
                .roastLevel(VALID_ROAST_LEVEL)
                .pricePerKg(DEFAULT_PRICE)
                .build();
    }

    public static CoffeeBeanDto buildBeanDto(UUID id, String name) {
        return CoffeeBeanDto.builder()
                .id(id)
                .name(name)
                .origin(VALID_ORIGIN)
                .roastLevel(VALID_ROAST_LEVEL)
                .pricePerKg(DEFAULT_PRICE)
                .build();
    }

    // --- Inventory Stock Fixtures ---
    public static InventoryStock buildStock(UUID id, Integer quantityGrams, CoffeeBean coffeeBean) {
        return InventoryStock.builder()
                .id(id)
                .quantityGrams(quantityGrams)
                .coffeeBean(coffeeBean)
                .build();
    }

    public static InventoryStockDto buildStockDto(UUID id, Integer quantityGrams, UUID coffeeBeanId) {
        return InventoryStockDto.builder()
                .id(id)
                .coffeeBeanId(coffeeBeanId)
                .quantityGrams(quantityGrams)
                .build();
    }

    // --- Supplier Fixtures ---
    public static Supplier buildSupplier(UUID id, String name, String contactEmail) {
        return Supplier.builder()
                .id(id)
                .name(name)
                .contactEmail(contactEmail)
                .build();
    }

    public static SupplierDto buildSupplierDto(UUID id, String name, String contactEmail) {
        return SupplierDto.builder()
                .id(id)
                .name(name)
                .contactEmail(contactEmail)
                .build();
    }
}