package com.briangabini.coffee_erp_backend.repositories;

import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {
}
