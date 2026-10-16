package br.com.terramove.api_esg.controller;

import br.com.terramove.api_esg.exception.ResourceNotFoundException;
import br.com.terramove.api_esg.model.AuditoriaCompliance;
import br.com.terramove.api_esg.repository.AuditoriaComplianceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
public class AuditoriaComplianceController {

    private final AuditoriaComplianceRepository repository;

    @GetMapping
    public List<AuditoriaCompliance> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AuditoriaCompliance buscar(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditoria não encontrada: " + id));
    }

    @PostMapping
    public ResponseEntity<AuditoriaCompliance> criar(@Valid @RequestBody AuditoriaCompliance auditoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(auditoria));
    }

    @PutMapping("/{id}")
    public AuditoriaCompliance atualizar(@PathVariable String id, @Valid @RequestBody AuditoriaCompliance dados) {
        AuditoriaCompliance auditoria = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditoria não encontrada: " + id));
        
        auditoria.setTitulo(dados.getTitulo());
        auditoria.setDescricao(dados.getDescricao());
        auditoria.setStatus(dados.getStatus());
        auditoria.setRegiaoId(dados.getRegiaoId());
        auditoria.setDataAuditoria(dados.getDataAuditoria());
        auditoria.setResponsavel(dados.getResponsavel());
        auditoria.setNormaReferencia(dados.getNormaReferencia());
        auditoria.setResultado(dados.getResultado());
        auditoria.setObservacoes(dados.getObservacoes());
        auditoria.setDataProximaAuditoria(dados.getDataProximaAuditoria());
        auditoria.setEvidencias(dados.getEvidencias());
        auditoria.setChecklist(dados.getChecklist());
        auditoria.setDataAtualizacao(LocalDateTime.now());
        
        return repository.save(auditoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Auditoria não encontrada: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
