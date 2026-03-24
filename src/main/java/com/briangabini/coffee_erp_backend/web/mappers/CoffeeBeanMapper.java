package com.briangabini.coffee_erp_backend.web.mappers;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.web.dto.CoffeeBeanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface CoffeeBeanMapper {

    CoffeeBeanDto coffeeBeanToCoffeeBeanDto(CoffeeBean coffeeBean);
    CoffeeBean coffeeBeanDtoToCoffeeBean(CoffeeBeanDto coffeeBeanDto);
}
