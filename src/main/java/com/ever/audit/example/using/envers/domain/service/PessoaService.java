package com.ever.audit.example.using.envers.domain.service;

import com.ever.audit.example.using.envers.api.dto.request.PessoaRequestDTO;
import com.ever.audit.example.using.envers.api.dto.request.PessoaUpdateRequestDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoCampoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.PessoaResponseDTO;
import com.ever.audit.example.using.envers.api.mapper.MapperConvert;
import com.ever.audit.example.using.envers.core.diffable.Diff;
import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import com.ever.audit.example.using.envers.domain.model.Pessoa;
import com.ever.audit.example.using.envers.domain.model.RevEntity;
import com.ever.audit.example.using.envers.domain.model.audit.HistoricoAlteracaoPessoa;
import com.ever.audit.example.using.envers.domain.repository.PessoaRepository;
import com.ever.audit.example.using.envers.domain.service.Utils.AppConstants;
import com.ever.audit.example.using.envers.domain.service.Utils.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final MapperConvert mapperConvert;

    public PessoaResponseDTO create(PessoaRequestDTO pessoa) {
        Pessoa pessoaRequest = mapperConvert.mapDtoToEntity(pessoa, Pessoa.class);
        pessoaRequest.setAtivo(Boolean.TRUE);
        return mapperConvert.mapEntityToDto(pessoaRepository.save(pessoaRequest), PessoaResponseDTO.class);
    }

    public PessoaResponseDTO findById(Long id) {
        Pessoa pessoaBanco = pessoaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Person not found for id: " + id));
        return mapperConvert.mapEntityToDto(pessoaBanco, PessoaResponseDTO.class);
    }

    public PessoaResponseDTO alterPeaple(Long id, PessoaUpdateRequestDTO requestDTO) {
        Pessoa pessoa = validPeaple(id);
        pessoa.setNome(requestDTO.getNome());
        pessoa.setNomeSocial(requestDTO.getNomeSocial());
        pessoa.setJustificativa(requestDTO.getJustificativa());
        return mapperConvert.mapEntityToDto(pessoaRepository.save(pessoa), PessoaResponseDTO.class);
    }

    public Pessoa validPeaple(Long id) {
        Pessoa savedPeaple = pessoaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Person not found for id: " + id));
        return savedPeaple;
    }

    public void deletePealple(Long id) {
        pessoaRepository.deletePeaple(id);
    }

    public List<PessoaResponseDTO> findAll() {
        return mapperConvert.collectionToDto(pessoaRepository.findAll(),PessoaResponseDTO.class);
    }

    public Page<HistoricoResponseDTO> pesquisarHistoricoAlteracaoPessoas(int page, int size, String field,
                                                                         String dateInitial, String finalDate) {
        int indiceRelatorio = 0;
        LocalDateTime periodoInicial = null;
        LocalDateTime periodoFinal = null;
        if (Objects.nonNull(dateInitial)) {
            periodoInicial = Data.stringToLocalDateTime(dateInitial, false);
        }
        if (Objects.nonNull(finalDate)) {
            periodoFinal = Data.stringToLocalDateTime(finalDate, true);
        }
        Data.validarDatas(periodoInicial, periodoFinal);
        List<HistoricoAlteracaoPessoa> historicoAlteracaoPessoas = pessoaRepository
                .pesquisar(Data.converterLocalDateTimeEmTimestamp(periodoInicial),
                        Data.converterLocalDateTimeEmTimestamp(periodoFinal));

        if (!historicoAlteracaoPessoas.isEmpty()
                && historicoAlteracaoPessoas.get(0).getRevtype().equals(1)) {
            addRegistroAnteriorAoPrimeiroNoHistoricoBD( historicoAlteracaoPessoas);
            indiceRelatorio = 1;
        }

        List<HistoricoResponseDTO> relatorioAlteracao = gerarRelatorio(historicoAlteracaoPessoas,
                indiceRelatorio);

        Pageable pageable = PageRequest.of(page, size);

        return paginarResultado(field, pageable, relatorioAlteracao);
    }

    private List<HistoricoResponseDTO> gerarRelatorio(List<HistoricoAlteracaoPessoa> listaHistorico, int indice) {
        List<HistoricoResponseDTO> relatorio = new ArrayList<>();
        int i = indice;
        for (; listaHistorico.size() > i; i++) {
            HistoricoAlteracaoPessoa historicoAlteracaoFerias = listaHistorico.get(i);
            Integer acaoHistorico = historicoAlteracaoFerias.getRevtype();
            RevEntity revisao = historicoAlteracaoFerias.getId().getRev();

            if (acaoHistorico.equals(AppConstants.UPDATE)) {
                HistoricoAlteracaoPessoa historicoAnterior = listaHistorico.get(i - 1);
                List<HistoricoResponseDTO> historicoOutputDTO = new Diff()
                        .gerarHistorico(historicoAnterior, historicoAlteracaoFerias, revisao);
                if (!historicoOutputDTO.isEmpty()) {
                    historicoOutputDTO.forEach(hist -> relatorio.add(hist));
                }
            }
        }
        return relatorio;
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

    private List<HistoricoAlteracaoPessoa> addRegistroAnteriorAoPrimeiroNoHistoricoBD(List<HistoricoAlteracaoPessoa> historicoAtual) {
        List<HistoricoAlteracaoPessoa> registroAnterior = pessoaRepository
                .buscaRegistroAnterior(historicoAtual.get(0).getId().getRev().getRev());
        HistoricoAlteracaoPessoa registro = registroAnterior.get(0);
        historicoAtual.add(0, registro);
        return historicoAtual;
    }

    public List<HistoricoCampoResponseDTO> buscarCamposAlterados(Long idPessoa) {
        List<HistoricoCampoResponseDTO> relatorioAlteracao = new ArrayList<>();
        List<HistoricoAlteracaoPessoa> historico = pessoaRepository
                .buscarPorId(idPessoa);

        for (int i = 0; historico.size() > i; i++) {
            HistoricoAlteracaoPessoa historicoAtual = historico.get(i);

            Integer acaoHistorico = historicoAtual.getRevtype();

            if (Objects.equals(acaoHistorico, AppConstants.UPDATE)) {
                HistoricoAlteracaoPessoa historicoAnterior = historico.get(i - 1);
                new Diff().buscarCamposAlterados(historicoAnterior, historicoAtual, relatorioAlteracao);
            }
        }

        return relatorioAlteracao;
    }
}
