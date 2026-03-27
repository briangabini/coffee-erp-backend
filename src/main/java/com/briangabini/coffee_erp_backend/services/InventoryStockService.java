package com.briangabini.coffee_erp_backend.services;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import com.briangabini.coffee_erp_backend.exceptions.ResourceNotFoundException;
import com.briangabini.coffee_erp_backend.repositories.CoffeeBeanRepository;
import com.briangabini.coffee_erp_backend.repositories.InventoryStockRepository;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import com.briangabini.coffee_erp_backend.web.mappers.InventoryStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryStockService {

    private final InventoryStockRepository inventoryStockRepository;
    private final CoffeeBeanRepository coffeeBeanRepository;
    private final InventoryStockMapper inventoryStockMapper;

    public List<InventoryStockDto> getAllStock() {
        log.info("Fetching all inventory stock");
        return inventoryStockRepository.findAll()
                .stream()
                .map(inventoryStockMapper::toInventoryStockDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryStockDto addStock(InventoryStockDto stockDto) {
        log.info("Adding new inventory stock for Coffee Bean ID: {}", stockDto.getCoffeeBeanId());

        CoffeeBean existingBean = coffeeBeanRepository.findById(stockDto.getCoffeeBeanId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot add stock: Coffee Bean not found with id: " + stockDto.getCoffeeBeanId()));
        InventoryStock newStock = inventoryStockMapper.toInventoryStock(stockDto);
        newStock.setCoffeeBean(existingBean);
        InventoryStock savedStock = inventoryStockRepository.save(newStock);

        return inventoryStockMapper.toInventoryStockDto(savedStock);
    }
}