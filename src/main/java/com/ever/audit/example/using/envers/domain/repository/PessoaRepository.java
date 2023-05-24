package com.ever.audit.example.using.envers.domain.repository;

import com.ever.audit.example.using.envers.domain.model.Pessoa;
import com.ever.audit.example.using.envers.domain.model.audit.HistoricoAlteracaoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa,Long> {

    @Modifying
    @Query(" update Pessoa set ativo = false where id = :id " )
    void deletePeaple(@Param("id") Long id);

    @Query(value = "select h.* FROM historico_alteracao_pessoa as h JOIN rev_auditoria ra ON ra.rev = h.rev " +
            "WHERE ((:periodoInicial IS NULL OR :periodoFinal IS NULL) OR (ra.timestemp BETWEEN :periodoInicial AND :periodoFinal)) " +
            " ORDER BY ra.timestemp DESC", nativeQuery = true)
    List<HistoricoAlteracaoPessoa> pesquisarPorPeriodo(
            @Param("periodoInicial") Timestamp periodoInicial,
            @Param("periodoFinal") Timestamp periodoFinal);

    @Query(value = "select * FROM historico_alteracao_pessoa as h JOIN rev_auditoria ra ON ra.rev = h.rev "
            + " AND ra.rev < :rev_id ORDER BY ra.rev DESC LIMIT 1", nativeQuery = true)
    HistoricoAlteracaoPessoa buscaRegistroAnterior(@Param("rev_id") Long id);
}
