package com.pucminas.aluguelcarros.application.mapper;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.domain.entity.Automovel;
import com.pucminas.aluguelcarros.domain.enums.TipoPropriedade;
import jakarta.inject.Singleton;

@Singleton
public class AutomovelMapper {

    public Automovel toEntity(AutomovelRequestDTO dto) {
        Automovel automovel = new Automovel();
        automovel.setMatricula(dto.matricula());
        automovel.setAno(dto.ano());
        automovel.setMarca(dto.marca());
        automovel.setModelo(dto.modelo());
        automovel.setPlaca(dto.placa());
        automovel.setProprietario(parseProprietario(dto.proprietario()));
        return automovel;
    }

    public AutomovelResponseDTO toResponseDTO(Automovel entity) {
        return new AutomovelResponseDTO(
                entity.getId(),
                entity.getMatricula(),
                entity.getAno(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getPlaca(),
                entity.isDisponivel(),
                entity.getProprietario().name()
        );
    }

    public void updateEntityFromDTO(AutomovelRequestDTO dto, Automovel entity) {
        entity.setMatricula(dto.matricula());
        entity.setAno(dto.ano());
        entity.setMarca(dto.marca());
        entity.setModelo(dto.modelo());
        entity.setPlaca(dto.placa());
        entity.setProprietario(parseProprietario(dto.proprietario()));
    }

    public String descricaoAutomovel(Automovel automovel) {
        return String.format("%s %s %d - %s", automovel.getMarca(), automovel.getModelo(),
                automovel.getAno(), automovel.getPlaca());
    }

    private TipoPropriedade parseProprietario(String valor) {
        if (valor == null || valor.isBlank()) {
            return TipoPropriedade.EMPRESA;
        }
        try {
            return TipoPropriedade.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TipoPropriedade.EMPRESA;
        }
    }
}
