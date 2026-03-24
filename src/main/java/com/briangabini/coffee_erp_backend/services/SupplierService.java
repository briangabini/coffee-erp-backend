package com.briangabini.coffee_erp_backend.services;

import com.briangabini.coffee_erp_backend.domain.Supplier;
import com.briangabini.coffee_erp_backend.repositories.SupplierRepository;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
import com.briangabini.coffee_erp_backend.web.mappers.SupplierMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public List<SupplierDto> getAllSuppliers() {
        log.info("Fetching all suppliers");
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toSupplierDto)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierById(UUID id) {
        log.info("Fetching supplier with id: {}", id);
        return supplierRepository.findById(id)
                .map(supplierMapper::toSupplierDto)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        log.info("Creating new supplier: {}", supplierDto.getName());

        Supplier supplierToSave = supplierMapper.toSupplier(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplierToSave);

        return supplierMapper.toSupplierDto(savedSupplier);
    }
}