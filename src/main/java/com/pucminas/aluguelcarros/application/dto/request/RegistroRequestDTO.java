package com.pucminas.aluguelcarros.application.dto.request;

import com.pucminas.aluguelcarros.domain.enums.Perfil;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Serdeable
public record RegistroRequestDTO(

        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres")
        String cpf,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotNull(message = "Perfil é obrigatório")
        Perfil perfil
) {
}
