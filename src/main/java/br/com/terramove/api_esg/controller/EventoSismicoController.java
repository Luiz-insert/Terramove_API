package br.com.terramove.api_esg.controller;

import br.com.terramove.api_esg.exception.ResourceNotFoundException;
import br.com.terramove.api_esg.model.EventoSismico;
import br.com.terramove.api_esg.repository.EventoSismicoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos-sismicos")
@RequiredArgsConstructor
public class EventoSismicoController {

    private final EventoSismicoRepository repository;

    @GetMapping
    public List<EventoSismico> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public EventoSismico buscar(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<EventoSismico> criar(@Valid @RequestBody EventoSismico evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(evento));
    }

    @PutMapping("/{id}")
    public EventoSismico atualizar(@PathVariable String id, @Valid @RequestBody EventoSismico dados) {
        EventoSismico evento = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado: " + id));
        evento.setTitulo(dados.getTitulo());
        evento.setMagnitude(dados.getMagnitude());
        evento.setLocalizacao(dados.getLocalizacao());
        evento.setDataOcorrencia(dados.getDataOcorrencia());
        evento.setDescricao(dados.getDescricao());
        evento.setMetadados(dados.getMetadados());
        return repository.save(evento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Evento não encontrado: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
