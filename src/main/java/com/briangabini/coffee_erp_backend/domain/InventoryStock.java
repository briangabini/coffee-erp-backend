package com.briangabini.coffee_erp_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inventory_stocks")
public class InventoryStock extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bean_id")
    private CoffeeBean coffeeBean;

    @NotNull
    @Min(0)
    private Integer quantityGrams;

    private LocalDate expiryDate;
}
