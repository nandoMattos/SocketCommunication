package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {
    private String operacao;
    private String token;

    public LogoutRequest(String token) {
        this.operacao = "logout";
        this.token = token;
    }
}
