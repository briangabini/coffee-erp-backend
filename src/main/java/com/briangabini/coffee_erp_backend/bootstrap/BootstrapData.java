package com.briangabini.coffee_erp_backend.bootstrap;

import com.briangabini.coffee_erp_backend.domain.CoffeeBean;
import com.briangabini.coffee_erp_backend.domain.InventoryStock;
import com.briangabini.coffee_erp_backend.domain.Supplier;
import com.briangabini.coffee_erp_backend.domain.enums.RoastLevel;
import com.briangabini.coffee_erp_backend.repositories.CoffeeBeanRepository;
import com.briangabini.coffee_erp_backend.repositories.InventoryStockRepository;
import com.briangabini.coffee_erp_backend.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final CoffeeBeanRepository coffeeBeanRepository;
    private final InventoryStockRepository inventoryStockRepository;

    @Override
    public void run(String... args) throws Exception {
        if (coffeeBeanRepository.count() == 0 && supplierRepository.count() == 0) {
            log.info("Database is empty. Bootstrapping initial ERP data...");

            List<Supplier> savedSuppliers = loadSuppliers();
            List<CoffeeBean> savedBeans = loadCoffeeBeans(savedSuppliers);
            loadInventoryStocks(savedBeans);

            log.info("Bootstrapping complete.");
        } else {
            log.info("Database already contains data. Skipping bootstrap.");
        }
    }

    private List<Supplier> loadSuppliers() {
        log.info("1. Loading Suppliers...");
        Supplier supplier1 = Supplier.builder()
                .name("Global Coffee Imports")
                .contactEmail("contact@globalcoffee.com")
                .build();

        Supplier supplier2 = Supplier.builder()
                .name("Andean Bean Co.")
                .contactEmail("sales@andeanbeans.co")
                .build();

        return supplierRepository.saveAll(Arrays.asList(supplier1, supplier2));
    }

    private List<CoffeeBean> loadCoffeeBeans(List<Supplier> suppliers) {
        log.info("2. Loading Coffee Beans and linking Suppliers...");

        Supplier globalCoffee = suppliers.get(0);
        Supplier andeanBeans = suppliers.get(1);

        CoffeeBean bean1 = CoffeeBean.builder()
                .name("Ethiopian Yirgacheffe")
                .origin("Ethiopia")
                .roastLevel(RoastLevel.LIGHT)
                .pricePerKg(new BigDecimal("22.50"))
                .build();
        bean1.addSupplier(globalCoffee);

        CoffeeBean bean2 = CoffeeBean.builder()
                .name("Colombian Supremo")
                .origin("Colombia")
                .roastLevel(RoastLevel.MEDIUM)
                .pricePerKg(new BigDecimal("18.00"))
                .build();
        bean2.addSupplier(andeanBeans);

        CoffeeBean bean3 = CoffeeBean.builder()
                .name("Sumatra Mandheling")
                .origin("Indonesia")
                .roastLevel(RoastLevel.DARK)
                .pricePerKg(new BigDecimal("19.75"))
                .build();
        bean3.addSupplier(globalCoffee);

        return coffeeBeanRepository.saveAll(Arrays.asList(bean1, bean2, bean3));
    }

    private void loadInventoryStocks(List<CoffeeBean> beans) {
        log.info("3. Loading Inventory Stock and linking to Beans...");

        CoffeeBean ethiopian = beans.get(0);
        CoffeeBean colombian = beans.get(1);

        InventoryStock stock1 = InventoryStock.builder()
                .coffeeBean(ethiopian)
                .quantityGrams(50000)
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        InventoryStock stock2 = InventoryStock.builder()
                .coffeeBean(colombian)
                .quantityGrams(75000)
                .expiryDate(LocalDate.now().plusMonths(8))
                .build();

        inventoryStockRepository.saveAll(Arrays.asList(stock1, stock2));
    }
}