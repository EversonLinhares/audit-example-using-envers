package com.ever.audit.example.using.envers.api.dto.response;

import lombok.Data;

@Data
public class PessoaResponseDTO {

    private Long id;
    private String nome;
    private String nomeSocial;
    private String nomePai;
    private String nomeMae;
    private Boolean ativo;
}
