package com.ever.audit.example.using.envers.api.dto.request;



import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PessoaRequestDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String nomeSocial;

    @NotBlank
    private String nomeMae;

    @NotBlank
    private String nomePai;
}
