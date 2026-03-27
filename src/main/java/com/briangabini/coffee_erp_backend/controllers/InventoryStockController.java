package com.briangabini.coffee_erp_backend.controllers;

import com.briangabini.coffee_erp_backend.services.InventoryStockService;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
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
    public ResponseEntity<List<InventoryStockDto>> getAllStock() {
        log.info("REST request to get all inventory stock");
        return ResponseEntity.ok(inventoryStockService.getAllStock());
    }

    @PostMapping
    public ResponseEntity<InventoryStockDto> addStock(@RequestBody InventoryStockDto inventoryStockDto) {
        log.info("REST request to add stock for coffee bean id: {}", inventoryStockDto.getCoffeeBeanId());

        InventoryStockDto savedStock = inventoryStockService.addStock(inventoryStockDto);

        // Best Practice: Return 201 Created with the Location header
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStock.getId())
                .encode()
                .toUri();

        return ResponseEntity.created(location).body(savedStock);
    }
}