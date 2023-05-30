package com.ever.audit.example.using.envers.domain.model.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "historico_alteracao_pessoa")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoAlteracaoPessoa implements Diffable<HistoricoAlteracaoPessoa> {

    @EmbeddedId
    private IdComposto id;

    @Column(name = "revtype")
    private Integer revtype;

    @Column(nullable = false, name = "nome")
    private String nome;

    @Column(nullable = false, name = "nome_social")
    private String nomeSocial;

    @Column(nullable = false, name = "nome_mae")
    private String nomeMae;

    @Column(nullable = false, name = "nome_pai")
    private String nomePai;

    @Column(name = "justificativa_edicao")
    private String justificativa;

    @Column(name = "ativo")
    private Boolean ativo;

    @Override
    public DiffResult<HistoricoAlteracaoPessoa> diff(HistoricoAlteracaoPessoa updateHist) {
        return new DiffBuilder<HistoricoAlteracaoPessoa>(this, updateHist, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("nome", this.nome, updateHist.nome)
                .append("nomeSocial", this.nomeSocial, updateHist.nomeSocial)
                .append("nomeMae", this.nomeMae, updateHist.nomeMae)
                .append("nomePai",this.nomePai, updateHist.nomePai)
                .append("justificativa", this.justificativa, updateHist.justificativa)
                .append("ativo", this.ativo, updateHist.ativo)
                .build();
    }

}
