package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcluirUsuarioRequest {
    private String operacao;
    private String token;
    private String ra;

    public ExcluirUsuarioRequest(String token, String ra) {
        this.operacao = "excluirUsuario";
        this.token = token;
        this.ra = ra;
    }
}
