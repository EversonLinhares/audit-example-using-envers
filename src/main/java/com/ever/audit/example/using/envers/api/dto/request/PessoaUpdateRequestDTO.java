package com.ever.audit.example.using.envers.api.dto.request;



import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PessoaUpdateRequestDTO {

    private String nome;

    private String nomeSocial;

    private String nomeMae;

    private String nomePai;

    private String justificativa;
}
