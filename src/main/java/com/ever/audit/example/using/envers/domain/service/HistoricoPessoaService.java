package com.ever.audit.example.using.envers.domain.service;

import com.ever.audit.example.using.envers.api.dto.response.DataHistoricoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoCampoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoResponseDTO;
import com.ever.audit.example.using.envers.core.diffable.Diff;
import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import com.ever.audit.example.using.envers.domain.model.Pessoa;
import com.ever.audit.example.using.envers.domain.model.RevEntity;
import com.ever.audit.example.using.envers.domain.service.Utils.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ever.audit.example.using.envers.domain.service.Utils.Data.validarDatas;
import static com.ever.audit.example.using.envers.domain.service.Utils.Validations.formatarDescricao;

@Service
@RequiredArgsConstructor
public class HistoricoPessoaService {

    private final EntityManager entityManager;
    private final Diff diff;

    public Page<HistoricoResponseDTO> findHistoryPeaple(Long id, int page, int size, String sortBy, String sortDir,
                                                        String field, String dateInitial, String finalDate) {
        LocalDateTime periodoInicial = null;
        LocalDateTime periodoFinal = null;
        if (Objects.nonNull(dateInitial)) {
            periodoInicial = Data.stringToLocalDateTime(dateInitial, false);
        }
        if (Objects.nonNull(finalDate)) {
            periodoFinal = Data.stringToLocalDateTime(finalDate, true);
        }
        validarDatas(periodoInicial, periodoFinal);

        List<HistoricoResponseDTO> allRevisions = new ArrayList<>();
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(Pessoa.class, id);

        for (int i = revisions.size() - 1; i > 0; i--) {
            Number currentRevisionNumber = revisions.get(i);
            Number previousRevisionNumber = revisions.get(i - 1);
            Pessoa currentRevision = auditReader
                    .find(Pessoa.class, id, currentRevisionNumber);
            Pessoa previousRevision = auditReader
                    .find(Pessoa.class, id, previousRevisionNumber);
            RevEntity currentRevisionRevEntity = auditReader.findRevision(RevEntity.class, currentRevisionNumber);
            List<HistoricoResponseDTO> changes = getChanges(previousRevision, currentRevision, currentRevisionRevEntity);
            allRevisions.addAll(changes);
        }

        List<HistoricoResponseDTO> filterRevisions = filterByDates(periodoInicial, periodoFinal, allRevisions);
        filterRevisions = filterByFields(field, filterRevisions);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        return paginarResultado(field, pageable, filterRevisions);
    }

    private Page<HistoricoResponseDTO> paginarResultado(String campo, Pageable pageable,
                                                        List<HistoricoResponseDTO> relatorioAlteracao) {
        List<HistoricoResponseDTO> pagina = relatorioAlteracao.stream()
                .filter(historicoOutputDTO -> Objects.isNull(campo) || historicoOutputDTO.getCampo().contains(campo))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), pagina.size());

        if (start > end) {
            start = 0;
            end = 0;
        }
        return new PageImpl<>(pagina.subList(start, end), pageable, pagina.size());
    }

    private List<HistoricoResponseDTO> getChanges(Diffable original, Diffable revision,
                                                  RevEntity currentrevisionRevEntity) {
        List<HistoricoResponseDTO> changes = new ArrayList<>();
        changes.addAll(diff.gerarHistorico(original, revision, currentrevisionRevEntity));
        return changes;
    }

    private static List<HistoricoResponseDTO> filterByDates(LocalDateTime periodoInicial, LocalDateTime periodoFinal,
                                                            List<HistoricoResponseDTO> allRevisions) {
        return allRevisions.stream().filter(revision -> {
            LocalDateTime revisionDateTime = revision.getDataAlteracao();
            boolean isAfterOrEqualStart = periodoInicial == null || revisionDateTime.isAfter(periodoInicial)
                    || revisionDateTime.isEqual(periodoInicial);
            boolean isBeforeOrEqualEnd = periodoFinal == null || revisionDateTime.isBefore(periodoFinal)
                    || revisionDateTime.isEqual(periodoFinal);
            return isAfterOrEqualStart && isBeforeOrEqualEnd;
        }).collect(Collectors.toList());
    }

    private static List<HistoricoResponseDTO> filterByFields(String campo, List<HistoricoResponseDTO> filteredRevisions) {
        if (campo != null) {
            filteredRevisions = filteredRevisions.stream()
                    .filter(revision -> revision.getCampo().equals(campo))
                    .collect(Collectors.toList());
        }
        return filteredRevisions;
    }

    public List<HistoricoCampoResponseDTO> buscarCamposAlterados(Long id) {
        List<HistoricoCampoResponseDTO> camposAlterados = new ArrayList<>();
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(Pessoa.class, id);

        for (int i = revisions.size() - 1; i > 0; i--) {
            Number currentRevisionNumber = revisions.get(i);
            Number previousRevisionNumber = revisions.get(i - 1);
            Pessoa currentRevision = auditReader
                    .find(Pessoa.class, id, currentRevisionNumber);
            Pessoa previousRevision = auditReader
                    .find(Pessoa.class, id, previousRevisionNumber);
            DiffResult<Pessoa> diffResult = currentRevision.diff(previousRevision);
            findFieldChange(diffResult, camposAlterados);
        }

        return camposAlterados;
    }

    private <T> void findFieldChange(DiffResult<T> diffResult,
                                           List<HistoricoCampoResponseDTO> camposAlterados) {
        populateField(diffResult, camposAlterados);
    }

    public <T> void populateField(DiffResult<T> diffResult,
                                                     List<HistoricoCampoResponseDTO> camposAlterados) {
        for (org.apache.commons.lang3.builder.Diff<?> diff : diffResult) {
            if (diff.getLeft() != null) {
                String campoAlterado = diff.getFieldName();
                String descricao = formatarDescricao(campoAlterado);
                HistoricoCampoResponseDTO historicoCampo = new HistoricoCampoResponseDTO();
                historicoCampo.setCampoAlterado(campoAlterado);
                historicoCampo.setDescricao(descricao);

                if (!camposAlterados.contains(historicoCampo)) {
                    camposAlterados.add(historicoCampo);
                }
            }
        }
    }

    public DataHistoricoResponseDTO buscaDataAlteracao(Long idPessoa) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(Pessoa.class, idPessoa);

        DataHistoricoResponseDTO dto = getDataHistoricoOutputDTO(idPessoa, auditReader, revisions);
        return dto;
    }

    protected static DataHistoricoResponseDTO getDataHistoricoOutputDTO(Long id, AuditReader auditReader, List<Number> revisions) {
        if (revisions.isEmpty()) {
            throw new ObjectNotFoundException("Não foi encontrado nenhum histórico com id fornecido. Id: " + id);
        }

        RevEntity lastRevision = null;
        RevEntity firistRevision = auditReader.findRevision(RevEntity.class, revisions.get(0));
        Integer ultimoIndice = revisions.size() - 1;
        if(ultimoIndice >= 1) {
            lastRevision = auditReader.findRevision(RevEntity.class, revisions.get(ultimoIndice));
        }
        DataHistoricoResponseDTO dataHistoricoOutputDTO = getDataHistoricoResponseDTO(firistRevision, lastRevision);
        return dataHistoricoOutputDTO;
    }

    private static DataHistoricoResponseDTO getDataHistoricoResponseDTO(RevEntity firistRevision, RevEntity lastRevision) {
        DataHistoricoResponseDTO dataHistoricoOutputDTO = new DataHistoricoResponseDTO();
        dataHistoricoOutputDTO.setIsCadastroCompleto(true);
        dataHistoricoOutputDTO.setUsuarioCriacao(firistRevision.getUsuario());
        dataHistoricoOutputDTO.setDataCriacao(LocalDateTime.ofInstant(Instant.ofEpochMilli(firistRevision.getTimestemp()),
                ZoneId.systemDefault()));
        dataHistoricoOutputDTO.setUsuarioUltimaAlteracao(lastRevision.getUsuario());
        dataHistoricoOutputDTO.setDataUltimaAlteracao(LocalDateTime.ofInstant(Instant.ofEpochMilli(lastRevision.getTimestemp()),
                ZoneId.systemDefault()));
        return dataHistoricoOutputDTO;
    }

}
