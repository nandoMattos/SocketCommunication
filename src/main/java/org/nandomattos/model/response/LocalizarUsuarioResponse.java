package org.nandomattos.model.response;

import lombok.*;
import org.nandomattos.model.dto.UserDTO;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizarUsuarioResponse {
    private Integer status;
    private String operacao;
    private UserDTO usuario;

    public LocalizarUsuarioResponse(UserDTO user) {
        this.status = 201;
        this.operacao = "localizarUsuario";
        this.usuario = user;
    }
}
