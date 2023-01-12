package com.ever.audit.example.using.envers.api.exceptionhandler;

import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MessageSource messageSource;

    public static final String MSG_ERRO_GENETICA_USUARIO_FINAL
            = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se" +
            " o problema persistir, entre em contato com o administrador do sistema";

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handleEntidadeNaoEncontrada(ObjectNotFoundException ex,
                                                         WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemTypeBean problemType = new ProblemTypeBean(ProblemType.RECURSO_NAO_ENCONTRADO, request.getContextPath());
        String detail = ex.getMessage();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        return handleValidationInternal(exception, exception.getBindingResult(), headers, status, request);
    }

    public ResponseEntity<Object> handleValidationInternal(Exception ex, BindingResult bindingResult, HttpHeaders headers,
                                                           HttpStatus status, WebRequest request) {

        ProblemTypeBean problemType = new ProblemTypeBean(ProblemType.DADOS_INVALIDOS, request.getContextPath());

        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        List<Problem.Object> problemObjects = bindingResult.getAllErrors().stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                    String name = objectError.getObjectName();
                    if (objectError instanceof FieldError) {
                        name = ((FieldError) objectError).getField();
                    }
                    return Problem.Object.builder()
                            .name(name)
                            .userMessage(message)
                            .build();
                })
                .collect(Collectors.toList());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .objects(problemObjects)
                .build();
        return handleExceptionInternal(ex, problem, headers, status, request);

    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers
            , HttpStatus status, WebRequest request) {
        if (body == null) {
            body = Problem.builder()
                    .timestamp(LocalDate.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .userMessage(MSG_ERRO_GENETICA_USUARIO_FINAL)
                    .build();
        } else if (body instanceof String) {
            body = Problem.builder()
                    .timestamp(LocalDate.now())
                    .title((String) body)
                    .status(status.value())
                    .userMessage(MSG_ERRO_GENETICA_USUARIO_FINAL)
                    .build();
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
    private Problem.ProblemBuilder createProblemBuilder(HttpStatus status,
                                                        ProblemTypeBean problemType, String detail) {
        return Problem.builder()
                .timestamp(LocalDate.now())
                .status(status.value())
                .type(problemType.getUrl())
                .title(problemType.getTitle())
                .detail(detail);
    }
}


