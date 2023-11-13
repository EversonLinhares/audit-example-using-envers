package com.ever.audit.example.using.envers.domain.model;

import lombok.*;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@AuditTable("historico_alteracao_pessoa")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Pessoa implements Diffable<Pessoa> {

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

    @Override
    public DiffResult<Pessoa> diff(Pessoa updateHist) {
        return new DiffBuilder<>(this, updateHist, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("nome", this.nome, updateHist.nome)
                .append("nomeSocial", this.nomeSocial, updateHist.nomeSocial)
                .append("nomeMae", this.nomeMae, updateHist.nomeMae)
                .append("nomePai",this.nomePai, updateHist.nomePai)
                .append("justificativa", this.justificativa, updateHist.justificativa)
                .append("ativo", this.ativo, updateHist.ativo)
                .build();
    }
}
