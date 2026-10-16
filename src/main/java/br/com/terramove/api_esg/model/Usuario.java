package br.com.terramove.api_esg.model;

import br.com.terramove.api_esg.model.enums.RoleUsuario;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    private String id;

    @NotBlank(message = "Username é obrigatório")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Password é obrigatória")
    private String password;

    private RoleUsuario role = RoleUsuario.ROLE_USER;

    private boolean ativo = true;

    // NoSQL: Perfil flexível do usuário (dados sociais, contato, preferências)
    private Map<String, Object> perfil;
}
