package br.com.terramove.api_esg.controller;

import br.com.terramove.api_esg.exception.ResourceNotFoundException;
import br.com.terramove.api_esg.model.RegiaoMonitorada;
import br.com.terramove.api_esg.repository.RegiaoMonitoradaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regioes")
@RequiredArgsConstructor
public class RegiaoMonitoradaController {

    private final RegiaoMonitoradaRepository repository;

    @GetMapping
    public List<RegiaoMonitorada> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public RegiaoMonitorada buscar(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Região não encontrada: " + id));
    }

    @PostMapping
    public ResponseEntity<RegiaoMonitorada> criar(@Valid @RequestBody RegiaoMonitorada regiao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(regiao));
    }

    @PutMapping("/{id}")
    public RegiaoMonitorada atualizar(@PathVariable String id, @Valid @RequestBody RegiaoMonitorada dados) {
        RegiaoMonitorada regiao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Região não encontrada: " + id));
        regiao.setNome(dados.getNome());
        regiao.setEstado(dados.getEstado());
        regiao.setAreaKm2(dados.getAreaKm2());
        regiao.setCoordenadasCentro(dados.getCoordenadasCentro());
        regiao.setNivelPreservacao(dados.getNivelPreservacao());
        regiao.setIndicadoresAmbientais(dados.getIndicadoresAmbientais());
        return repository.save(regiao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Região não encontrada: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
