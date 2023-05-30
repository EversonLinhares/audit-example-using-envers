package com.ever.audit.example.using.envers.domain.repository;

import com.ever.audit.example.using.envers.domain.model.Pessoa;
import com.ever.audit.example.using.envers.domain.model.audit.HistoricoAlteracaoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa,Long> {

    @Modifying
    @Query(" update Pessoa set ativo = false where id = :id " )
    void deletePeaple(@Param("id") Long id);
    
    @Query("Select h from HistoricoAlteracaoPessoa h inner join RevEntity re on re.rev = h.id.rev " +
           " where ((:periodoInicial IS NULL OR :periodoFinal IS NULL) OR (re.timestemp BETWEEN :periodoInicial and :periodoFinal )) " +
           " ORDER BY re.timestemp DESC ")
    List<HistoricoAlteracaoPessoa> pesquisar(
            @Param("periodoInicial") Long periodoInicial,
            @Param("periodoFinal") Long periodoFinal);

    @Query(value = "select * FROM historico_alteracao_pessoa as h JOIN rev_auditoria ra ON ra.rev = h.rev "
            + " AND ra.rev < :rev_id ORDER BY ra.rev DESC LIMIT 1", nativeQuery = true)
    HistoricoAlteracaoPessoa buscaRegistroAnterior(@Param("rev_id") Long id);
}
