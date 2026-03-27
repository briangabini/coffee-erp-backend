package com.briangabini.coffee_erp_backend.controllers;

import com.briangabini.coffee_erp_backend.services.CoffeeBeanService;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/api/v1/coffee-beans")
@RequiredArgsConstructor
public class CoffeeBeanController {

    private final CoffeeBeanService coffeeBeanService;

    @GetMapping
    public ResponseEntity<List<CoffeeBeanDto>> getAllBeans() {
        log.info("REST request to get all coffee beans");
        return ResponseEntity.ok(coffeeBeanService.getAllBeans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoffeeBeanDto> getBeanById(@PathVariable UUID id) {
        return getCoffeeBeanDtoResponseEntity(id);
    }

    private @NotNull ResponseEntity<CoffeeBeanDto> getCoffeeBeanDtoResponseEntity(UUID id) {
        log.info("REST request to get coffee bean by id: {}", id);
        return ResponseEntity.ok(coffeeBeanService.getBeanById(id));
    }

    @PostMapping
    public ResponseEntity<CoffeeBeanDto> createBean(@RequestBody CoffeeBeanDto coffeeBeanDto) {
        log.info("REST request to create new coffee bean: {}", coffeeBeanDto.getName());

        CoffeeBeanDto savedBean = coffeeBeanService.createBean(coffeeBeanDto);

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-uri-building.html#mvc-servleturicomponentsbuilder
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBean.getId())
                .encode()
                .toUri();

        return ResponseEntity.created(location).body(savedBean);
    }
}
