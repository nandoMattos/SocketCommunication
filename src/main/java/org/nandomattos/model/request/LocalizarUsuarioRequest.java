package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizarUsuarioRequest {
    private String operacao;
    private String token;
    private String ra;

    public LocalizarUsuarioRequest(String token, String ra){
        this.operacao = "localizarUsuario";
        this.token = token;
        this.ra = ra;
    }
}
