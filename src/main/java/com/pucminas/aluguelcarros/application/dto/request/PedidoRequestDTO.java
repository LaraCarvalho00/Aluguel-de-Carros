package com.pucminas.aluguelcarros.application.dto.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record PedidoRequestDTO(

        @NotNull(message = "ID do cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "ID do automóvel é obrigatório")
        Long automovelId,

        @NotNull(message = "Data de início é obrigatória")
        String dataInicio,

        @NotNull(message = "Data de fim é obrigatória")
        String dataFim
) {
}
