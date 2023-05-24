package com.ever.audit.example.using.envers.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoricoResponseDTO {

    private Long idAuditoria;
    private String responsavel;
    private LocalDateTime dataAlteracao;
    private String campo;
    private String descricao;
    private String situacaoAnterior;
    private String situacaoAtual;
}
