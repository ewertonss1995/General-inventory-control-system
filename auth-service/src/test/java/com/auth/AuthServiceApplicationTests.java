package com.auth;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Desativado para build rápido do container Docker sem necessidade de recursos externos")
@SpringBootTest
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
