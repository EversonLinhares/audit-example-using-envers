package com.ever.audit.example.using.envers.domain.service;

import com.ever.audit.example.using.envers.domain.exception.NegocioException;
import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import com.ever.audit.example.using.envers.domain.model.Documento;
import com.ever.audit.example.using.envers.domain.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ever.audit.example.using.envers.domain.service.Utils.Validations.verifyExtension;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    public Documento create (MultipartFile file) throws IOException {
        try {
            verifyExtension(file);
            byte[] fileBytes = file.getBytes();
            Documento newDocument = Documento.builder()
                    .nome(file.getOriginalFilename())
                    .file(fileBytes)
                    .build();
            return documentoRepository.save(newDocument);
        }catch (IOException e){
            throw new NegocioException(e.getMessage());
        }
    }

    public Documento findById(Long id) {
        Documento document = documentoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No document found with id : " + id));
        return document;
    }
}
