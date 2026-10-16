package br.com.terramove.api_esg;

import br.com.terramove.api_esg.model.EventoSismico;
import br.com.terramove.api_esg.repository.EventoSismicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// Teste de integração com MongoDB real. Roda no pipeline (serviço mongo do GitHub Actions)
// ou localmente quando MONGODB_URI estiver definida.
@SpringBootTest(properties = "app.seed.enabled=false")
@EnabledIfEnvironmentVariable(named = "MONGODB_URI", matches = ".+")
class EventoSismicoIntegrationTest {

    @Autowired
    private EventoSismicoRepository repository;

    @Test
    void salvaEBuscaNoMongo() {
        EventoSismico salvo = repository.save(EventoSismico.builder()
                .titulo("Integração")
                .magnitude(3.3)
                .localizacao("Teste CI")
                .dataOcorrencia(LocalDateTime.now())
                .build());

        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.findById(salvo.getId()))
                .get()
                .extracting(EventoSismico::getTitulo)
                .isEqualTo("Integração");

        repository.deleteById(salvo.getId());
    }
}
