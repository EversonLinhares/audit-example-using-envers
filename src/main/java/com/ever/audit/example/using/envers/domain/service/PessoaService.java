package com.ever.audit.example.using.envers.domain.service;

import com.ever.audit.example.using.envers.api.dto.request.PessoaRequestDTO;
import com.ever.audit.example.using.envers.api.dto.response.PessoaResponseDTO;
import com.ever.audit.example.using.envers.api.mapper.MapperConvert;
import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import com.ever.audit.example.using.envers.domain.model.Pessoa;
import com.ever.audit.example.using.envers.domain.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final MapperConvert mapperConvert;

    public PessoaResponseDTO create(PessoaRequestDTO pessoa) {
        Pessoa pessoaSave = pessoaRepository.save(mapperConvert.mapDtoToEntity(pessoa, Pessoa.class));
        return mapperConvert.mapEntityToDto(pessoaSave,PessoaResponseDTO.class);
    }

    public PessoaResponseDTO findById(Long id) {
        Pessoa pessoaBanco = pessoaRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Pessoa não encontrada para id: " + id));
        return mapperConvert.mapEntityToDto(pessoaBanco,PessoaResponseDTO.class);
    }
}
