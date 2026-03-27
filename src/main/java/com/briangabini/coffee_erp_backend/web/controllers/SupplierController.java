package com.briangabini.coffee_erp_backend.web.controllers;

import com.briangabini.coffee_erp_backend.services.SupplierService;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        log.info("REST request to get all suppliers");
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable UUID id) {
        log.info("REST request to get supplier by id: {}", id);
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@RequestBody SupplierDto supplierDto) {
        log.info("REST request to create new supplier: {}", supplierDto.getName());

        SupplierDto savedSupplier = supplierService.createSupplier(supplierDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedSupplier.getId())
                .encode()
                .toUri();

        return ResponseEntity.created(location).body(savedSupplier);
    }
}