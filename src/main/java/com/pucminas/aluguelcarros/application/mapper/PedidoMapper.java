package com.pucminas.aluguelcarros.application.mapper;

import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import com.pucminas.aluguelcarros.domain.entity.Pedido;
import jakarta.inject.Singleton;

@Singleton
public class PedidoMapper {

    private final AutomovelMapper automovelMapper;

    public PedidoMapper(AutomovelMapper automovelMapper) {
        this.automovelMapper = automovelMapper;
    }

    public PedidoResponseDTO toResponseDTO(Pedido entity) {
        return new PedidoResponseDTO(
                entity.getId(),
                entity.getCliente().getId(),
                entity.getCliente().getNome(),
                entity.getAutomovel().getId(),
                automovelMapper.descricaoAutomovel(entity.getAutomovel()),
                entity.getStatus().name(),
                entity.getStatus().getDescricao(),
                entity.getDataInicio().toString(),
                entity.getDataFim().toString(),
                entity.getParecer(),
                entity.getDataCriacao() != null ? entity.getDataCriacao().toString() : null,
                entity.getDataAtualizacao() != null ? entity.getDataAtualizacao().toString() : null
        );
    }
}
