package com.ever.audit.example.using.envers.api.controller;

import com.ever.audit.example.using.envers.api.dto.request.PessoaRequestDTO;
import com.ever.audit.example.using.envers.api.dto.request.PessoaUpdateRequestDTO;
import com.ever.audit.example.using.envers.api.dto.response.HistoricoResponseDTO;
import com.ever.audit.example.using.envers.api.dto.response.PessoaResponseDTO;
import com.ever.audit.example.using.envers.domain.service.PessoaService;
import com.ever.audit.example.using.envers.domain.service.Utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pessoa")
public class PessoaController {

    private final PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> create (@RequestBody @Valid PessoaRequestDTO pessoa){
        return ResponseEntity.ok().body(pessoaService.create(pessoa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(pessoaService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> alterPeaple (@PathVariable Long id, @RequestBody PessoaUpdateRequestDTO requestDTO) {
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

    @GetMapping("/historico-alteracao")
    public ResponseEntity<Page<HistoricoResponseDTO>> pesquisarHistoricoAlteracaoPessoas (
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_NUMERO_PAGINA, required = false) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_TOTAL_PAGINA, required = false) int size,
            @RequestParam(value = "dataInicial", required = false) String dataInicial,
            @RequestParam(value = "dataFinal", required = false) String dataFinal,
            @RequestParam(value = "campo", required = false) String campo) {

        Page<HistoricoResponseDTO> pageHistoricoAlteracoes = pessoaService
                .pesquisarHistoricoAlteracaoPessoas(page, size, campo, dataInicial, dataFinal);

        if (pageHistoricoAlteracoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok().body(pageHistoricoAlteracoes);
    }
}
