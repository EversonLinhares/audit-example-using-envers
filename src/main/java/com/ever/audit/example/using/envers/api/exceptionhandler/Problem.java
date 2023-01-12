package com.ever.audit.example.using.envers.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class Problem {
    private Integer status;

    private String type;

    private String title;

    private String detail;

    private String userMessage;

    private LocalDate timestamp;

    private List<Object> objects;

    @Builder
    @Getter
    @Setter
    public static class Object {

        private String name;

        private String userMessage;
    }
}
