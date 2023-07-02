package com.ever.audit.example.using.envers.domain.repository;

import com.ever.audit.example.using.envers.domain.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento,Long> {
}
