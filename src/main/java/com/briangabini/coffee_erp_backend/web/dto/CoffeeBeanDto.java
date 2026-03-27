package com.briangabini.coffee_erp_backend.web.dto;

import com.briangabini.coffee_erp_backend.domain.enums.RoastLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeBeanDto {

    private UUID id;
    private Integer version;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;

    @NotBlank(message = "Bean name is required")
    private String name;

    @NotBlank(message = "Origin is required")
    String origin;

    @NotNull(message = "Roast level is required")
    private RoastLevel roastLevel;

    @NotNull(message = "Price per kg is required")
    @Min(0)
    private BigDecimal pricePerKg;
}
