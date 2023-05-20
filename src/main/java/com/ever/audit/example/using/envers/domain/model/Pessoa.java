package com.ever.audit.example.using.envers.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@AuditTable("historico_alteracao_pessoa")
@EntityListeners(AuditingEntityListener.class)
@Builder
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

    @Column(name = "justificativa_edicao")
    private String justificativa;

    @Column
    private Boolean ativo;

}
