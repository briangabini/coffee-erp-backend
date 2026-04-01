package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.security.permissions.InventoryStockCreatePermission;
import com.briangabini.coffee_erp_backend.security.permissions.InventoryStockReadPermission;
import com.briangabini.coffee_erp_backend.services.InventoryStockService;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryStockController {

    private final InventoryStockService inventoryStockService;

    @GetMapping
    @InventoryStockReadPermission
    public ResponseEntity<List<InventoryStockDto>> getAllStock() {
        log.info("REST request to get all inventory stock");
        return ResponseEntity.ok(inventoryStockService.getAllStock());
    }

    @PostMapping
    @InventoryStockCreatePermission
    public ResponseEntity<InventoryStockDto> addStock(@Valid @RequestBody InventoryStockDto inventoryStockDto) {
        log.info("REST request to add stock for coffee bean id: {}", inventoryStockDto.getCoffeeBeanId());

        InventoryStockDto savedStock = inventoryStockService.addStock(inventoryStockDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStock.getId())
                .encode()
                .toUri();

        return ResponseEntity.created(location).body(savedStock);
    }
}