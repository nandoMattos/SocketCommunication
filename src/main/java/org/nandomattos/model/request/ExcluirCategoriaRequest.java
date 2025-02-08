package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcluirCategoriaRequest {
    private String operacao = "excluirCategoria";
    private String token;
    private Integer id;

    public ExcluirCategoriaRequest(String token, Integer id) {
        this.token = token;
        this.id = id;
    }
}