package br.com.terramove.api_esg.controller;

import br.com.terramove.api_esg.exception.ResourceNotFoundException;
import br.com.terramove.api_esg.model.AlertaAmbiental;
import br.com.terramove.api_esg.repository.AlertaAmbientalRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaAmbientalController {

    private final AlertaAmbientalRepository repository;

    @GetMapping
    public List<AlertaAmbiental> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AlertaAmbiental buscar(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<AlertaAmbiental> criar(@Valid @RequestBody AlertaAmbiental alerta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(alerta));
    }

    @PutMapping("/{id}")
    public AlertaAmbiental atualizar(@PathVariable String id, @Valid @RequestBody AlertaAmbiental dados) {
        AlertaAmbiental alerta = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + id));
        alerta.setTitulo(dados.getTitulo());
        alerta.setDescricao(dados.getDescricao());
        alerta.setNivelRisco(dados.getNivelRisco());
        alerta.setStatus(dados.getStatus());
        alerta.setAreaAfetada(dados.getAreaAfetada());
        alerta.setMedidasRecomendadas(dados.getMedidasRecomendadas());
        alerta.setDataResolucao(dados.getDataResolucao());
        alerta.setPropriedadesEspecificas(dados.getPropriedadesEspecificas());
        return repository.save(alerta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Alerta não encontrado: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
