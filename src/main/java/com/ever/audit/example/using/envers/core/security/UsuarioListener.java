package com.ever.audit.example.using.envers.core.security;
import org.hibernate.envers.RevisionListener;

public class UsuarioListener implements RevisionListener{

    @Override
    public void newRevision(Object revisionEntity) {
        RevEntity revEntity = (RevEntity) revisionEntity;
        revEntity.setUsuario("Usuario Logado pessoa teste");
        revEntity.setTimestemp(System.currentTimeMillis());
    }
}
