package com.ever.audit.example.using.envers.domain.service.Utils;

import com.ever.audit.example.using.envers.domain.exception.NegocioException;
import com.ever.audit.example.using.envers.domain.model.Documento;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

public class Validations {

    public static void verifyExtension(MultipartFile file) {
        if(file.isEmpty()){
            throw new NegocioException("The file is empty");
        }
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            if (!extension.matches("(?i)pdf")) {
                throw new NegocioException("Only .pdf files are allowed");
            }

    }

    public static HttpHeaders createHeaders(Documento document) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentDisposition(ContentDisposition.attachment()
                .filename(String.format(document.getNome())).build());
        return responseHeaders;
    }

    public static String formatarDescricao(String descricao) {
        String output = descricao.replaceAll("(\\p{Lu})", " $1").trim();
        output = output.replaceAll("(\\d)", " $1").trim().toLowerCase();
        output = output.substring(0, 1).toUpperCase() + output.substring(1);
        return adicionarAcentuacao(output);
    }

    public static String adicionarAcentuacao(String string) {
        return string.replace("mae", "mãe");
    }
}
