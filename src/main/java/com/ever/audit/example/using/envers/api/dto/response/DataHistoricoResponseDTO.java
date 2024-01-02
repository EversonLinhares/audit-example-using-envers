package com.ever.audit.example.using.envers.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataHistoricoResponseDTO {
    private String usuarioCriacao;
    private LocalDateTime dataCriacao;
    private String usuarioUltimaAlteracao;
    private LocalDateTime dataUltimaAlteracao;
    private Boolean isCadastroCompleto = false;
}
