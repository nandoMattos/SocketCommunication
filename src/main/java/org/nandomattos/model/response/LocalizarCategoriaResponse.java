package org.nandomattos.model.response;

import lombok.*;
import org.nandomattos.entity.Categoria;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocalizarCategoriaResponse {
    private Integer status = 201;
    private String operacao = "listarCategoria";
    private Categoria categoria;

    public LocalizarCategoriaResponse(Categoria categoria) {
        this.categoria = categoria;
    }
}
