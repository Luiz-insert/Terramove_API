package br.com.terramove.api_esg;

import br.com.terramove.api_esg.model.*;
import br.com.terramove.api_esg.model.enums.*;
import br.com.terramove.api_esg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// Só roda quando app.seed.enabled=true (local e staging). Em produção fica desligado.
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EventoSismicoRepository eventoRepository;
    private final AlertaAmbientalRepository alertaRepository;
    private final RegiaoMonitoradaRepository regiaoRepository;
    private final AuditoriaComplianceRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Limpar dados antigos para teste
        eventoRepository.deleteAll();
        alertaRepository.deleteAll();
        regiaoRepository.deleteAll();
        auditoriaRepository.deleteAll();
        usuarioRepository.deleteAll();

        loadUsuarios();
        loadRegioes();
        loadEventos();
        loadAlertas();
        loadAuditorias();
    }

    private void loadUsuarios() {
        for (int i = 1; i <= 10; i++) {
            usuarioRepository.save(Usuario.builder()
                    .username("usuario" + i)
                    .password(passwordEncoder.encode("senha" + i))
                    .role(i == 1 ? RoleUsuario.ROLE_ADMIN : RoleUsuario.ROLE_USER)
                    .ativo(true)
                    .perfil(Map.of("cargo", "Analista " + i, "departamento", "ESG"))
                    .build());
        }
    }

    private void loadRegioes() {
        String[] estados = {"SP", "MG", "RJ", "AM", "BA", "PR", "SC", "RS", "MT", "GO"};
        for (int i = 1; i <= 10; i++) {
            regiaoRepository.save(RegiaoMonitorada.builder()
                    .nome("Reserva Ecológica " + i)
                    .estado(estados[i-1])
                    .areaKm2(500.0 + (i * 100))
                    .coordenadasCentro(Arrays.asList(-23.5505, -46.6333))
                    .nivelPreservacao("ALTO")
                    .indicadoresAmbientais(Map.of("umidade", 80, "co2", 400))
                    .build());
        }
    }

    private void loadEventos() {
        for (int i = 1; i <= 10; i++) {
            eventoRepository.save(EventoSismico.builder()
                    .titulo("Evento Sísmico #" + i)
                    .magnitude(2.0 + (i * 0.5))
                    .localizacao("Zona de Falha " + i)
                    .dataOcorrencia(LocalDateTime.now().minusDays(i))
                    .descricao("Tremor detectado automaticamente pelo sensor S-" + i)
                    .metadados(Map.of("sensorId", "SN-" + i, "profundidade", 10 * i))
                    .build());
        }
    }

    private void loadAlertas() {
        for (int i = 1; i <= 10; i++) {
            alertaRepository.save(AlertaAmbiental.builder()
                    .titulo("Alerta de Risco " + i)
                    .descricao("Risco potencial detectado na área norte")
                    .nivelRisco(i % 2 == 0 ? NivelRisco.ALTO : NivelRisco.MODERADO)
                    .status(StatusAlerta.ATIVO)
                    .areaAfetada("Perímetro " + i)
                    .medidasRecomendadas("Evacuação imediata do setor " + i)
                    .propriedadesEspecificas(Map.of("populacaoAfetada", 100 * i))
                    .build());
        }
    }

    private void loadAuditorias() {
        for (int i = 1; i <= 10; i++) {
            auditoriaRepository.save(AuditoriaCompliance.builder()
                    .titulo("Auditoria ISO 14001 - Ciclo " + i)
                    .descricao("Verificação anual de normas ambientais")
                    .status(StatusAuditoria.PENDENTE)
                    .responsavel("Auditor Externo " + i)
                    .normaReferencia("ISO 14001:2015")
                    .evidencias(Arrays.asList("doc1.pdf", "foto_campo.jpg"))
                    .checklist(Arrays.asList(
                            new AuditoriaCompliance.ChecklistItem("Controle de Resíduos", true, "OK"),
                            new AuditoriaCompliance.ChecklistItem("Emissões", false, "Ajustar filtros")
                    ))
                    .build());
        }
    }
}
