package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CadastroUsuarioRequest {
    private String operacao;
    private String ra;
    private String senha;
    private String nome;

    public CadastroUsuarioRequest(String ra, String senha, String nome) {
        this.operacao = "cadastrarUsuario";
        this.ra = ra;
        this.senha = senha;
        this.nome = nome;
    }
}
