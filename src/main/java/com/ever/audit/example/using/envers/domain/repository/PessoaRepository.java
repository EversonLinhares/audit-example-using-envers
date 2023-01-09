package com.ever.audit.example.using.envers.domain.repository;

import com.ever.audit.example.using.envers.domain.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa,Long> {
}
