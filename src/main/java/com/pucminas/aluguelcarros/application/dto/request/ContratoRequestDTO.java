package com.pucminas.aluguelcarros.application.dto.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Serdeable
public record ContratoRequestDTO(

        @NotNull(message = "ID do pedido é obrigatório")
        Long pedidoId,

        @NotNull(message = "Valor total é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor total deve ser positivo")
        BigDecimal valorTotal,

        @NotNull(message = "Taxa de juros é obrigatória")
        @DecimalMin(value = "0.0", message = "Taxa de juros não pode ser negativa")
        BigDecimal taxaJuros,

        @NotNull(message = "Número de parcelas é obrigatório")
        @Min(value = 1, message = "Mínimo de 1 parcela")
        Integer parcelas,

        @NotBlank(message = "Banco agente é obrigatório")
        String bancoAgente
) {
}
