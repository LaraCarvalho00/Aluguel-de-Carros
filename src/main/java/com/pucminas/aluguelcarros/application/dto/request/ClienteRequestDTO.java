package com.pucminas.aluguelcarros.application.dto.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Serdeable
public record ClienteRequestDTO(

        @NotBlank(message = "RG é obrigatório")
        String rg,

        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres")
        String cpf,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Endereço é obrigatório")
        String endereco,

        @NotBlank(message = "Profissão é obrigatória")
        String profissao,

        @Size(max = 3, message = "Máximo de 3 entidades empregadoras permitidas")
        List<String> entidadesEmpregadoras,

        @NotNull(message = "Rendimentos é obrigatório")
        @DecimalMin(value = "0.0", message = "Rendimentos não pode ser negativo")
        BigDecimal rendimentos
) {
}
