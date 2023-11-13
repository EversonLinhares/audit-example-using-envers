package com.ever.audit.example.using.envers.domain.model;

import com.ever.audit.example.using.envers.core.security.UserListener;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rev_auditoria")
@RevisionEntity(UserListener.class)
public class RevEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long rev;

    @RevisionTimestamp
    private Long timestemp;

    private String usuario;
}
