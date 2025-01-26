package org.nandomattos.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String ra;
    private String senha;
    private String nome;
}
