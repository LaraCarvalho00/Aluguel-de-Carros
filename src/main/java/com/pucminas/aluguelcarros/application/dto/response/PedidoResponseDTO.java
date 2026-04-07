package com.pucminas.aluguelcarros.application.dto.response;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PedidoResponseDTO(
        Long id,
        Long clienteId,
        String clienteNome,
        Long automovelId,
        String automovelDescricao,
        String status,
        String statusDescricao,
        String dataInicio,
        String dataFim,
        String parecer,
        String dataCriacao,
        String dataAtualizacao
) {
}
