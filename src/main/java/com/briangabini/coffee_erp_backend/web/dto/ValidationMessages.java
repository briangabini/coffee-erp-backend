package com.briangabini.coffee_erp_backend.web.dto;

public final class ValidationMessages {

    private ValidationMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // --- Common Validation Messages ---
    public static final String MUST_BE_GREATER_THAN_OR_EQUAL_TO_0 = "must be greater than or equal to 0";

    // --- Coffee Bean Messages ---
    public static final String BEAN_NAME_REQUIRED = "Bean name is required";
    public static final String ORIGIN_REQUIRED = "Origin is required";
    public static final String ROAST_LEVEL_REQUIRED = "Roast level is required";
    public static final String PRICE_REQUIRED = "Price per kg is required";

    // --- Inventory Stock Messages ---
    public static final String COFFEE_BEAN_ID_REQUIRED = "Coffee Bean ID is required";
    public static final String QUANTITY_REQUIRED = "Quantity is required";
}