package com.ever.audit.example.using.envers.api.controller;

import com.ever.audit.example.using.envers.api.dto.request.PessoaRequestDTO;
import com.ever.audit.example.using.envers.api.dto.response.PessoaResponseDTO;
import com.ever.audit.example.using.envers.api.mapper.MapperConvert;
import com.ever.audit.example.using.envers.domain.service.PessoaService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<PessoaResponseDTO> alterPeaple (@PathVariable Long id, @RequestBody PessoaRequestDTO requestDTO) {
        return ResponseEntity.ok().body(pessoaService.alterPeaple(id,requestDTO));
    }

    @DeleteMapping("/{id}")
    public void deletePeaple (@PathVariable Long id){
        pessoaService.deletePealple(id);
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> findAll(){
        return ResponseEntity.ok().body(pessoaService.findAll());
    }
}
