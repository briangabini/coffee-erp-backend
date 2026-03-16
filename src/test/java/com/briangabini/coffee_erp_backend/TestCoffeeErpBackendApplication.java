package com.briangabini.coffee_erp_backend;

import org.springframework.boot.SpringApplication;

public class TestCoffeeErpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(CoffeeErpBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
