package com.ever.audit.example.using.envers.core.diffable;

import com.ever.audit.example.using.envers.api.dto.response.HistoricoCampoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoResponseDTO;
import com.ever.audit.example.using.envers.domain.model.RevEntity;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Diff {

    public List<HistoricoResponseDTO> gerarHistorico(final Diffable objetoAntigo, final Diffable objetoNovo,
                                                     RevEntity rev) {
        List<HistoricoResponseDTO> relatorioDeAlteracao = new ArrayList<>();
        try {
            DiffResult<?> diffResult = objetoAntigo.diff(objetoNovo);
            for (org.apache.commons.lang3.builder.Diff<?> diff : diffResult) {
                HistoricoResponseDTO historicoAlteracaoDTO = HistoricoResponseDTO.builder().build();
                historicoAlteracaoDTO.setCampo(diff.getFieldName());
                historicoAlteracaoDTO.setDescricao(diff.getFieldName());
                historicoAlteracaoDTO.setSituacaoAnterior(validar(diff.getLeft()));
                historicoAlteracaoDTO.setSituacaoAtual(validar(diff.getRight()));
                historicoAlteracaoDTO.setDataAlteracao(new Timestamp(rev.getTimestemp()).toLocalDateTime());
                historicoAlteracaoDTO.setResponsavel(rev.getUsuario());
                historicoAlteracaoDTO.setIdAuditoria(rev.getRev());
                relatorioDeAlteracao.add(historicoAlteracaoDTO);
            }

        } catch (Exception e) {
            e.getMessage();
        }
        return relatorioDeAlteracao;
    }

    public static String validar(Object campo) {
        return Objects.nonNull(campo) ? campo.toString() : "";
    }

    public void buscarCamposAlterados(final Diffable objetoAntigo, final Diffable objetoNovo,
                                      List<HistoricoCampoResponseDTO> camposAlterados) {

        try {
            DiffResult<?> diffResult = objetoAntigo.diff(objetoNovo);

            for (org.apache.commons.lang3.builder.Diff<?> diff : diffResult) {

                HistoricoCampoResponseDTO historicoAlteracaoDTO = new HistoricoCampoResponseDTO();
                historicoAlteracaoDTO.setCampoAlterado(diff.getFieldName());
                historicoAlteracaoDTO.setDescricao(diff.getFieldName());

                if (!camposAlterados.contains(historicoAlteracaoDTO) && !Objects.equals(historicoAlteracaoDTO.getCampoAlterado(), "justificativa")) {
                    camposAlterados.add(historicoAlteracaoDTO);
                }
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }
}
