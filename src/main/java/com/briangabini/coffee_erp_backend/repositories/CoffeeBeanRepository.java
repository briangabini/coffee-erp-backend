package com.briangabini.coffee_erp_backend.repositories;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoffeeBeanRepository extends JpaRepository<CoffeeBean, UUID> {
}
