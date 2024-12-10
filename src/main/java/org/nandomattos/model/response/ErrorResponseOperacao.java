package org.nandomattos.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseOperacao {
    private int status;
    private String operacao;
    private String mensagem;
}
