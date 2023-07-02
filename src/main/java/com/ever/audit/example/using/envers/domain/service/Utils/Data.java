package com.ever.audit.example.using.envers.domain.service.Utils;

import com.ever.audit.example.using.envers.domain.exception.NegocioException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Data {
    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FINAL_DATE_NULL = "A data final não pode estar vazia.";
    private static final String INITIAL_DATE_NULL = "A data inicial não pode estar vazia.";
    private static final String FINAL_DATE_BEFORE_INITIAL_DATE = "A data fim não pode ser anterior a data Inicio";
    private static final Integer MAX_HOUR = 23;
    private static final Integer MAX_MIN = 59;
    private static final Integer MAX_SEC = 59;

    public static LocalDateTime stringToLocalDateTime(String data, Boolean isDataFinal) {
        if(isDataFinal) {
            return LocalDate.parse(data, CUSTOM_FORMATTER).atTime(MAX_HOUR, MAX_MIN, MAX_SEC);
        }
        return LocalDate.parse(data, CUSTOM_FORMATTER).atTime(0, 0,0 );
    }

    public static void validarDatas(LocalDateTime periodoInicial, LocalDateTime periodoFinal) {
        validaDataInicialFinal(Objects.nonNull(periodoInicial), Objects.isNull(periodoFinal), FINAL_DATE_NULL);
        validaDataInicialFinal(Objects.isNull(periodoInicial), Objects.nonNull(periodoFinal), INITIAL_DATE_NULL);
        validaDataInicialFinal(periodoInicial, periodoFinal, FINAL_DATE_BEFORE_INITIAL_DATE);
    }

    private static void validaDataInicialFinal(LocalDateTime periodoInicial, LocalDateTime periodoFinal, String mensagem) {
        if (Objects.nonNull(periodoFinal) && Objects.nonNull(periodoFinal)){
            if (periodoFinal.isBefore(periodoInicial)) {
                throw new NegocioException(mensagem);
            }
        }
    }

    private static void validaDataInicialFinal(boolean periodoInicial, boolean periodoFinal, String mensagem) {
        if (periodoInicial && periodoFinal) throw new NegocioException(mensagem);
    }

    public static Long converterLocalDateTimeEmTimestamp(LocalDateTime data) {
        try {
            if(data != null) {
                return Timestamp.valueOf(data).getTime();
            }
            return null;
        } catch (Exception e) {
            throw new NegocioException("Data inválidas.");
        }
    }
}
