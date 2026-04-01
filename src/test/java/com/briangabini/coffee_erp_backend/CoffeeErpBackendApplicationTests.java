package com.briangabini.coffee_erp_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {
		"application.security.jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
})
class CoffeeErpBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
