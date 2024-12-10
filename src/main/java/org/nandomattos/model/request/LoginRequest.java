package org.nandomattos.model.request;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String operacao;
    private String ra;
    private String senha;

    public LoginRequest(String ra, String senha) {
        this.operacao = "login";
        this.ra = ra;
        this.senha = senha;
    }
}