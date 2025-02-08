package org.nandomattos.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizarCategoriaRequest {
    private String operacao = "localizarCategoria";
    private String token;
    private Integer id;

    public LocalizarCategoriaRequest(String token, Integer id) {
        this.token = token;
        this.id = id;
    }
}
