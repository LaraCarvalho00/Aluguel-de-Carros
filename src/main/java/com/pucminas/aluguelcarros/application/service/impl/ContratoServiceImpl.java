package com.pucminas.aluguelcarros.application.service.impl;

import com.pucminas.aluguelcarros.application.dto.request.ContratoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ContratoResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.ContratoMapper;
import com.pucminas.aluguelcarros.application.service.IContratoService;
import com.pucminas.aluguelcarros.domain.entity.Contrato;
import com.pucminas.aluguelcarros.domain.entity.Pedido;
import com.pucminas.aluguelcarros.domain.enums.StatusPedido;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.ContratoRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.PedidoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Singleton
public class ContratoServiceImpl implements IContratoService {

    private final ContratoRepository contratoRepository;
    private final PedidoRepository pedidoRepository;
    private final ContratoMapper contratoMapper;

    public ContratoServiceImpl(ContratoRepository contratoRepository,
                               PedidoRepository pedidoRepository,
                               ContratoMapper contratoMapper) {
        this.contratoRepository = contratoRepository;
        this.pedidoRepository = pedidoRepository;
        this.contratoMapper = contratoMapper;
    }

    @Override
    @Transactional
    public ContratoResponseDTO criar(ContratoRequestDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", dto.pedidoId()));

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new BusinessException("Só é possível criar contrato para pedidos aprovados");
        }

        contratoRepository.findByPedidoId(dto.pedidoId()).ifPresent(c -> {
            throw new BusinessException("Já existe um contrato para este pedido");
        });

        Contrato contrato = new Contrato();
        contrato.setPedido(pedido);
        contrato.setValorTotal(dto.valorTotal());
        contrato.setTaxaJuros(dto.taxaJuros());
        contrato.setParcelas(dto.parcelas());
        contrato.setBancoAgente(dto.bancoAgente());
        contrato.setDataCriacao(LocalDateTime.now());

        pedido.setStatus(StatusPedido.CONTRATADO);
        pedido.setDataAtualizacao(LocalDateTime.now());
        pedidoRepository.update(pedido);

        Contrato salvo = contratoRepository.save(contrato);
        return contratoMapper.toResponseDTO(salvo);
    }

    @Override
    public ContratoResponseDTO buscarPorId(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", id));
        return contratoMapper.toResponseDTO(contrato);
    }

    @Override
    public ContratoResponseDTO buscarPorPedido(Long pedidoId) {
        Contrato contrato = contratoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", "pedido " + pedidoId));
        return contratoMapper.toResponseDTO(contrato);
    }

    @Override
    public List<ContratoResponseDTO> listarTodos() {
        return StreamSupport.stream(contratoRepository.findAll().spliterator(), false)
                .map(contratoMapper::toResponseDTO)
                .toList();
    }
}
