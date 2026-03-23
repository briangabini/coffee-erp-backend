package com.briangabini.coffee_erp_backend.domain;

import com.briangabini.coffee_erp_backend.domain.enums.RoastLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "coffee_beans")
public class CoffeeBean extends BaseEntity {

    @NotBlank
    String name;

    @NotNull
    private String origin;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private RoastLevel roastLevel;

    @NotNull
    @Min(0)
    private BigDecimal pricePerKg;

    @Setter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(name = "coffee_bean_supplier")
    @Builder.Default
    private Set<Supplier> suppliers = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "coffeeBean", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<InventoryStock> inventoryStocks = new HashSet<>();

    public void addSupplier(Supplier supplier) {
        this.suppliers.add(supplier);
        supplier.getCoffeeBeans().add(this);
    }

    public void removeSupplier(Supplier supplier) {
        this.suppliers.remove(supplier);
        supplier.getCoffeeBeans().remove(this);
    }

    public void addInventoryStock(InventoryStock stock) {
        this.inventoryStocks.add(stock);
        stock.setCoffeeBean(this);
    }

    public void removeInventoryStock(InventoryStock stock) {
        this.inventoryStocks.remove(stock);
        stock.setCoffeeBean(null);
    }
}
