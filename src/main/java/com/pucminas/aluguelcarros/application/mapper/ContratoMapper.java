package com.pucminas.aluguelcarros.application.mapper;

import com.pucminas.aluguelcarros.application.dto.response.ContratoResponseDTO;
import com.pucminas.aluguelcarros.domain.entity.Contrato;
import jakarta.inject.Singleton;

@Singleton
public class ContratoMapper {

    private final AutomovelMapper automovelMapper;

    public ContratoMapper(AutomovelMapper automovelMapper) {
        this.automovelMapper = automovelMapper;
    }

    public ContratoResponseDTO toResponseDTO(Contrato entity) {
        return new ContratoResponseDTO(
                entity.getId(),
                entity.getPedido().getId(),
                entity.getPedido().getCliente().getNome(),
                automovelMapper.descricaoAutomovel(entity.getPedido().getAutomovel()),
                entity.getValorTotal(),
                entity.getTaxaJuros(),
                entity.getParcelas(),
                entity.getBancoAgente(),
                entity.getDataCriacao() != null ? entity.getDataCriacao().toString() : null
        );
    }
}
