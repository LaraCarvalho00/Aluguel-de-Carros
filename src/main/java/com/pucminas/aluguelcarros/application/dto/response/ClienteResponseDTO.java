package com.pucminas.aluguelcarros.application.dto.response;

import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.util.List;

@Serdeable
public record ClienteResponseDTO(
        Long id,
        String rg,
        String cpf,
        String nome,
        String endereco,
        String profissao,
        List<String> entidadesEmpregadoras,
        BigDecimal rendimentos
) {
}
