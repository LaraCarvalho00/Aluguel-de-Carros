package com.pucminas.aluguelcarros.application.dto.response;

import com.pucminas.aluguelcarros.domain.enums.Perfil;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UsuarioResponseDTO(
        Long id,
        String cpf,
        Perfil perfil
) {
}
