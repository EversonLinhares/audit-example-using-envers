package com.ever.audit.example.using.envers.domain.repository;

import com.ever.audit.example.using.envers.domain.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PessoaRepository extends JpaRepository<Pessoa,Long> {

    @Modifying
    @Query(" update Pessoa set ativo = false where id = :id " )
    void deletePeaple(@Param("id") Long id);
}
