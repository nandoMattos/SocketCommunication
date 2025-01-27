package org.nandomattos.model.request;

import lombok.*;
import org.nandomattos.entity.Categoria;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalvarCategoriaRequest {
    private String operacao;
    private String token;
    private Categoria categoria;

    public SalvarCategoriaRequest(String token, Categoria categoria) {
        this.operacao = "salvarCategoria";
        this.token = token;
        this.categoria = categoria;
    }
}
