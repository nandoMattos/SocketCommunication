package org.nandomattos.model.response;

import lombok.*;
import org.nandomattos.entity.Categoria;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListarCategoriasResponse {
    private Integer status = 201;
    private String operacao = "listarCategorias";
    private List<Categoria> categorias;

    public ListarCategoriasResponse(List<Categoria> categorias) {
        this.categorias = categorias;
    }
}
