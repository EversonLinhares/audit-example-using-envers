package com.ever.audit.example.using.envers.core.security;
import com.ever.audit.example.using.envers.domain.model.RevEntity;
import org.hibernate.envers.RevisionListener;

public class UserListener implements RevisionListener{

    @Override
    public void newRevision(Object revisionEntity) {
        RevEntity revEntity = (RevEntity) revisionEntity;
        revEntity.setUsuario("Usuario Logado pessoa teste");
        revEntity.setTimestemp(System.currentTimeMillis());
    }
}
