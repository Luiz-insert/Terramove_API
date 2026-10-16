package br.com.terramove.api_esg.controller;

import br.com.terramove.api_esg.exception.ResourceNotFoundException;
import br.com.terramove.api_esg.model.Usuario;
import br.com.terramove.api_esg.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(usuario));
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable String id, @Valid @RequestBody Usuario dados) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        
        usuario.setUsername(dados.getUsername());
        if (dados.getPassword() != null && !dados.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dados.getPassword()));
        }
        usuario.setRole(dados.getRole());
        usuario.setAtivo(dados.isAtivo());
        usuario.setPerfil(dados.getPerfil());
        
        return repository.save(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
