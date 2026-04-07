package com.pucminas.aluguelcarros.application.dto.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record ParecerRequestDTO(

        @NotNull(message = "Aprovação é obrigatória")
        Boolean aprovado,

        @NotBlank(message = "Parecer é obrigatório")
        String parecer
) {
}
