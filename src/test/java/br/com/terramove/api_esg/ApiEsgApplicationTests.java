package br.com.terramove.api_esg;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Seed desligado: o contexto sobe sem precisar de um MongoDB acessível
@SpringBootTest(properties = "app.seed.enabled=false")
class ApiEsgApplicationTests {

	@Test
	void contextLoads() {
	}

}
