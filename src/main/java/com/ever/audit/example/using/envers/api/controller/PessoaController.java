package com.ever.audit.example.using.envers.api.controller;

import com.ever.audit.example.using.envers.api.dto.request.PessoaRequestDTO;
import com.ever.audit.example.using.envers.api.dto.request.PessoaUpdateRequestDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoCampoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.PessoaResponseDTO;
import com.ever.audit.example.using.envers.domain.service.HistoricoPessoaService;
import com.ever.audit.example.using.envers.domain.service.PessoaService;
import com.ever.audit.example.using.envers.domain.service.Utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pessoa")
public class PessoaController {

    private final PessoaService pessoaService;
    private final HistoricoPessoaService historicoPessoaService;

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> create (@RequestBody @Valid PessoaRequestDTO pessoa){
        PessoaResponseDTO pessoaResponseDTO = pessoaService.create(pessoa);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(pessoaResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(pessoaService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> alterPeaple (@PathVariable Long id,
                                                          @RequestBody PessoaUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok().body(pessoaService.alterPeaple(id,requestDTO));
    }

    @DeleteMapping("/{id}")
    public void deletePeaple (@PathVariable Long id){
        pessoaService.deletePealple(id);
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> findAll(){
        List<PessoaResponseDTO> pessoas = pessoaService.findAll();
        if( pessoas.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(pessoaService.findAll());
    }

    @GetMapping("/historico-alteracao/{id}")
    public ResponseEntity<Page<HistoricoResponseDTO>> historyPaged (
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_NUMERO_PAGINA, required = false) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_TOTAL_PAGINA, required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECAO, required = false) String sortDir,
            @RequestParam(value = "initialDate", required = false) String initialDate,
            @RequestParam(value = "finalDate", required = false) String finalDate,
            @RequestParam(value = "field", required = false) String field) {

        Page<HistoricoResponseDTO> pageHistoricoAlteracoes = historicoPessoaService
                .findHistoryPeaple(id, page, size, sortBy, sortDir, field, initialDate, finalDate);

        if (pageHistoricoAlteracoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok().body(pageHistoricoAlteracoes);
    }

    @GetMapping("/historico-alteracao/campos/{idPessoa}")
    public ResponseEntity<List<HistoricoCampoResponseDTO>> buscarCamposAlterados(@PathVariable(name = "idPessoa") Long idPessoa) {
        List<HistoricoCampoResponseDTO> listaCampos = historicoPessoaService.buscarCamposAlterados(idPessoa);

        if (listaCampos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listaCampos);
    }
}
