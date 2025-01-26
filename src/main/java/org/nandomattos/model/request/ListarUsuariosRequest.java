package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListarUsuariosRequest {
    private String operacao;
    private String token;

    public ListarUsuariosRequest(String token){
        this.operacao = "listarUsuarios";
        this.token = token;
    }
}
