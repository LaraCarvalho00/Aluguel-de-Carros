package com.pucminas.aluguelcarros.application.dto.response;

import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;

@Serdeable
public record ContratoResponseDTO(
        Long id,
        Long pedidoId,
        String clienteNome,
        String automovelDescricao,
        BigDecimal valorTotal,
        BigDecimal taxaJuros,
        Integer parcelas,
        String bancoAgente,
        String dataCriacao
) {
}
