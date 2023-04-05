package com.ever.audit.example.using.envers.domain.service;

import com.ever.audit.example.using.envers.api.dto.request.PessoaRequestDTO;
import com.ever.audit.example.using.envers.api.dto.response.PessoaResponseDTO;
import com.ever.audit.example.using.envers.api.mapper.MapperConvert;
import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import com.ever.audit.example.using.envers.domain.model.Pessoa;
import com.ever.audit.example.using.envers.domain.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        Pessoa pessoaBanco = pessoaRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Person not found for id: " + id));
        return mapperConvert.mapEntityToDto(pessoaBanco,PessoaResponseDTO.class);
    }

    public PessoaResponseDTO alterPeaple(Long id, PessoaRequestDTO requestDTO) {
        Pessoa pessoa = validPeaple(id);
        pessoa.setNome(requestDTO.getNome());
        pessoa.setNomeSocial(requestDTO.getNomeSocial());
        pessoa.setNomeMae(requestDTO.getNomeMae());
        pessoa.setNomePai(requestDTO.getNomePai());
        return mapperConvert.mapEntityToDto(pessoaRepository.save(pessoa),PessoaResponseDTO.class);
    }

    public Pessoa validPeaple(Long id) {
        Pessoa savedPeaple = pessoaRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Person not found for id: " + id));
        return savedPeaple;
    }

    public void deletePealple(Long id) {
        pessoaRepository.deletePeaple(id);
    }

    public List<PessoaResponseDTO> findAll() {
        return pessoaRepository.findAll().stream().map(p -> mapperConvert.mapEntityToDto(p, PessoaResponseDTO.class)).collect(Collectors.toList());
    }
}
