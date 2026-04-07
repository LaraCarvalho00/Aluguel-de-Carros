package com.pucminas.aluguelcarros.application.dto.response;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AutomovelResponseDTO(
        Long id,
        String matricula,
        Integer ano,
        String marca,
        String modelo,
        String placa,
        boolean disponivel,
        String proprietario
) {
}
