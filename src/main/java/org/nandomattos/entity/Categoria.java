package org.nandomattos.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="categoria")
public class Categoria {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    private String nome;
}
