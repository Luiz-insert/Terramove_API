package br.com.terramove.api_esg.controller;

import br.com.terramove.api_esg.model.EventoSismico;
import br.com.terramove.api_esg.repository.EventoSismicoRepository;
import br.com.terramove.api_esg.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoSismicoController.class)
@Import(SecurityConfig.class)
class EventoSismicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoSismicoRepository repository;

    private EventoSismico evento(String id) {
        return EventoSismico.builder()
                .id(id)
                .titulo("Tremor teste")
                .magnitude(4.5)
                .localizacao("Zona de Falha 1")
                .dataOcorrencia(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
    }

    @Test
    void listarEhPublico() throws Exception {
        when(repository.findAll()).thenReturn(List.of(evento("1"), evento("2")));

        mockMvc.perform(get("/api/eventos-sismicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Tremor teste"));
    }

    @Test
    void buscarInexistenteRetorna404() throws Exception {
        when(repository.findById("x")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/eventos-sismicos/x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void criarSemAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(post("/api/eventos-sismicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        verify(repository, never()).save(any());
    }

    @Test
    void criarComAutenticacaoRetorna201() throws Exception {
        when(repository.save(any(EventoSismico.class))).thenAnswer(inv -> {
            EventoSismico e = inv.getArgument(0);
            e.setId("novo-id");
            return e;
        });

        mockMvc.perform(post("/api/eventos-sismicos")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Novo tremor","magnitude":5.2,
                                 "localizacao":"Zona 9","dataOcorrencia":"2026-02-01T10:00:00"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("novo-id"));
    }

    @Test
    void criarComDadosInvalidosRetorna400() throws Exception {
        mockMvc.perform(post("/api/eventos-sismicos")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"magnitude\":15}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").exists())
                .andExpect(jsonPath("$.campos.magnitude").exists());
    }

    @Test
    void deletarInexistenteRetorna404() throws Exception {
        when(repository.existsById("x")).thenReturn(false);

        mockMvc.perform(delete("/api/eventos-sismicos/x").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }
}
