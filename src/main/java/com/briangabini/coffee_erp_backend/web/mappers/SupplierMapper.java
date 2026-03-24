package com.briangabini.coffee_erp_backend.web.mappers;

import com.briangabini.coffee_erp_backend.domain.Supplier;
import com.briangabini.coffee_erp_backend.web.dto.SupplierDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface SupplierMapper {

    SupplierDto toSupplierDto(Supplier supplier);
    Supplier toSupplier(SupplierDto dto);
}