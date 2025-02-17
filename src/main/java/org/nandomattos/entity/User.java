package org.nandomattos.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString
public class User {

    @Id
    @Column(name = "ra")
    private String ra;

    @Column(name = "senha")
    private String senha;

    @Column(name = "nome")
    private String nome;

    @Column(name = "admin")
    private Boolean admin;

    @Column(name = "logado")
    private Boolean logado;
}
