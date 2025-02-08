package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListarCategoriasRequest {
    private String operacao;
    private String token;

    public ListarCategoriasRequest(String token) {
        this.operacao = "listarCategorias";
        this.token = token;
    }
}
