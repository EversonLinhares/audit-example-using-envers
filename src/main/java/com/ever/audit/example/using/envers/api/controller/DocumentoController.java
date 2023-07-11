package com.ever.audit.example.using.envers.api.controller;

import com.ever.audit.example.using.envers.domain.model.Documento;
import com.ever.audit.example.using.envers.domain.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static com.ever.audit.example.using.envers.domain.service.Utils.Validations.createHeaders;

@RestController
@RequiredArgsConstructor
@RequestMapping("documento")
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestParam MultipartFile file) throws IOException {
        Documento doc = documentoService.create(file);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(doc.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPDF(@PathVariable("id") Long id ) {
        Documento document =  documentoService.findById(id);
        if (Objects.nonNull(document) && Objects.nonNull(document.getFile())) {
            return ResponseEntity.ok()
                    .headers(createHeaders(document))
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(document.getFile());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
