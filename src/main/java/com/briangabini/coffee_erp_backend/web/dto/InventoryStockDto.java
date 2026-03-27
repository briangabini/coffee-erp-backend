package com.briangabini.coffee_erp_backend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryStockDto {

    private UUID id;
    private Integer version;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;

    @NotNull(message = ValidationMessages.COFFEE_BEAN_ID_REQUIRED)
    private UUID coffeeBeanId;

    @NotNull(message = ValidationMessages.QUANTITY_REQUIRED)
    @Min(0)
    private Integer quantityGrams;

    private LocalDate expiryDate;
}