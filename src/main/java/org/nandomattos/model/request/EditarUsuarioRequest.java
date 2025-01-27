package org.nandomattos.model.request;

import lombok.*;
import org.nandomattos.model.dto.UserDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditarUsuarioRequest {
    private String operacao;
    private String token;
    private UserDTO usuario;

    public EditarUsuarioRequest(String token, UserDTO userDTO) {
        this.operacao = "editarUsuario";
        this.token = token;
        this.usuario = userDTO;
    }
}
