package com.ever.audit.example.using.envers.api.dto.request;



import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PessoaUpdateRequestDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String nomeSocial;

    @NotBlank
    private String justificativa;
}
