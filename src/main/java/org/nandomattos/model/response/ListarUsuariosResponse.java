package org.nandomattos.model.response;

import lombok.*;
import org.nandomattos.entity.User;
import org.nandomattos.model.dto.UserDTO;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListarUsuariosResponse {
    private Integer status;
    private String operacao;
    private List<UserDTO> usuarios;

    public ListarUsuariosResponse(List<UserDTO> usuarios) {
        this.status = 201;
        this.operacao = "listarUsuarios";
        this.usuarios = usuarios;
    }
}
