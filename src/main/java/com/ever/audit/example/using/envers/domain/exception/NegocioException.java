package com.ever.audit.example.using.envers.domain.exception;

import java.io.Serializable;

public class NegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem,causa);
    }
}
