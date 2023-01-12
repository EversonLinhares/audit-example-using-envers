package com.ever.audit.example.using.envers.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemType {
    DADOS_INVALIDOS("/dados-invalidos", "Dados inválidos"),
    RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado");

    private String title;
    private String path;

    ProblemType(String path, String title) {
        this.path = path;
        this.title = title;
    }
}
