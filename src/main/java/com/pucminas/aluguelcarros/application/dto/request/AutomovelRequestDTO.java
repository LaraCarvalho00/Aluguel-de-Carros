package com.pucminas.aluguelcarros.application.dto.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record AutomovelRequestDTO(

        @NotBlank(message = "Matrícula é obrigatória")
        String matricula,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 1900, message = "Ano deve ser maior que 1900")
        @Max(value = 2030, message = "Ano inválido")
        Integer ano,

        @NotBlank(message = "Marca é obrigatória")
        String marca,

        @NotBlank(message = "Modelo é obrigatório")
        String modelo,

        @NotBlank(message = "Placa é obrigatória")
        String placa,

        String proprietario
) {
}
