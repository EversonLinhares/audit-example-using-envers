package com.ever.audit.example.using.envers.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoCampoResponseDTO {
    private String campoAlterado;
    private String descricao;
}
