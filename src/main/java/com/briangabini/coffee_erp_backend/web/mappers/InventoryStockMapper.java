package com.briangabini.coffee_erp_backend.web.mappers;

import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import com.briangabini.coffee_erp_backend.web.dto.InventoryStockDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface InventoryStockMapper {

    @Mapping(source = "coffeeBean.id", target = "coffeeBeanId")
    InventoryStockDto inventoryStockToInventoryStockDto(InventoryStock inventoryStock);

    @Mapping(target = "coffeeBean", ignore = true)
    InventoryStock inventoryStockDtoToInventoryStock(InventoryStockDto dto);
}
