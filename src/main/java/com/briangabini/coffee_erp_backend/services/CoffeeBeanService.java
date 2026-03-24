package com.briangabini.coffee_erp_backend.services;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.repositories.CoffeeBeanRepository;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import com.briangabini.coffee_erp_backend.web.mappers.CoffeeBeanMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoffeeBeanService {

    private final CoffeeBeanRepository coffeeBeanRepository;
    private final CoffeeBeanMapper coffeeBeanMapper;

    public List<CoffeeBeanDto> getAllBeans() {
        log.info("Fetching all coffee beans");
        return coffeeBeanRepository.findAll()
                .stream()
                .map(coffeeBeanMapper::toCoffeeBeanDto)
                .collect(Collectors.toList());
    }

    public CoffeeBeanDto getBeanById(UUID id) {
        log.info("Fetching coffee bean by id");
        return coffeeBeanRepository.findById(id)
                .map(coffeeBeanMapper::toCoffeeBeanDto)
                .orElseThrow(() -> new RuntimeException("Coffee Bean not found with id: " + id));
    }

    @Transactional
    public CoffeeBeanDto createBean(CoffeeBeanDto coffeeBeanDto) {
        log.info("Creating new coffee bean: {}", coffeeBeanDto.getName());

        CoffeeBean beanToSave = coffeeBeanMapper.toCoffeeBean(coffeeBeanDto);
        CoffeeBean savedBean = coffeeBeanRepository.save(beanToSave);

        return coffeeBeanMapper.toCoffeeBeanDto(savedBean);
    }
}
