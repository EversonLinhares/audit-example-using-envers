package com.ever.audit.example.using.envers.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(name = "nome_social")
    private String nomeSocial;

    @Column(length = 80, name = "nome_mae")
    private String nomeMae;

    @Column(length = 80, name = "nome_pai")
    private String nomePai;

}
