package com.ever.audit.example.using.envers.domain.repository;

import com.ever.audit.example.using.envers.domain.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento,Long> {
    Optional<Documento> findByFileHash(String fileHash);
}
