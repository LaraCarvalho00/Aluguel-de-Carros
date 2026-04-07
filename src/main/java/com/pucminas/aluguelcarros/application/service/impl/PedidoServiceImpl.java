package com.pucminas.aluguelcarros.application.service.impl;

import com.pucminas.aluguelcarros.application.dto.request.ParecerRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.PedidoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.PedidoMapper;
import com.pucminas.aluguelcarros.application.service.IPedidoService;
import com.pucminas.aluguelcarros.domain.entity.Automovel;
import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.domain.entity.Pedido;
import com.pucminas.aluguelcarros.domain.enums.StatusPedido;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.AutomovelRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.PedidoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Singleton
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final AutomovelRepository automovelRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             ClienteRepository clienteRepository,
                             AutomovelRepository automovelRepository,
                             PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.automovelRepository = automovelRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Override
    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.clienteId()));

        Automovel automovel = automovelRepository.findById(dto.automovelId())
                .orElseThrow(() -> new ResourceNotFoundException("Automóvel", dto.automovelId()));

        if (!automovel.isDisponivel()) {
            throw new BusinessException("Automóvel não está disponível para aluguel");
        }

        LocalDate dataInicio = LocalDate.parse(dto.dataInicio());
        LocalDate dataFim = LocalDate.parse(dto.dataFim());

        if (dataFim.isBefore(dataInicio) || dataFim.isEqual(dataInicio)) {
            throw new BusinessException("A data de fim deve ser posterior à data de início");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setDataInicio(dataInicio);
        pedido.setDataFim(dataFim);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataCriacao(LocalDateTime.now());

        automovel.setDisponivel(false);
        automovelRepository.update(automovel);

        Pedido salvo = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(salvo);
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return StreamSupport.stream(pedidoRepository.findAll().spliterator(), false)
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", clienteId);
        }
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public PedidoResponseDTO avaliar(Long id, ParecerRequestDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new BusinessException("Pedido não pode ser avaliado no status atual: " + pedido.getStatus().getDescricao());
        }

        pedido.setParecer(dto.parecer());
        pedido.setStatus(dto.aprovado() ? StatusPedido.APROVADO : StatusPedido.REPROVADO);
        pedido.setDataAtualizacao(LocalDateTime.now());

        if (!dto.aprovado()) {
            liberarAutomovel(pedido);
        }

        Pedido atualizado = pedidoRepository.update(pedido);
        return pedidoMapper.toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (pedido.getStatus() == StatusPedido.CONTRATADO) {
            throw new BusinessException("Pedido já contratado não pode ser cancelado");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já está cancelado");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setDataAtualizacao(LocalDateTime.now());
        liberarAutomovel(pedido);

        Pedido atualizado = pedidoRepository.update(pedido);
        return pedidoMapper.toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Só é possível modificar pedidos com status Pendente");
        }

        LocalDate dataInicio = LocalDate.parse(dto.dataInicio());
        LocalDate dataFim = LocalDate.parse(dto.dataFim());
        if (dataFim.isBefore(dataInicio) || dataFim.isEqual(dataInicio)) {
            throw new BusinessException("A data de fim deve ser posterior à data de início");
        }

        if (!pedido.getAutomovel().getId().equals(dto.automovelId())) {
            liberarAutomovel(pedido);

            Automovel novoAutomovel = automovelRepository.findById(dto.automovelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Automóvel", dto.automovelId()));
            if (!novoAutomovel.isDisponivel()) {
                throw new BusinessException("Automóvel não está disponível para aluguel");
            }
            novoAutomovel.setDisponivel(false);
            automovelRepository.update(novoAutomovel);
            pedido.setAutomovel(novoAutomovel);
        }

        pedido.setDataInicio(dataInicio);
        pedido.setDataFim(dataFim);
        pedido.setDataAtualizacao(LocalDateTime.now());

        Pedido atualizado = pedidoRepository.update(pedido);
        return pedidoMapper.toResponseDTO(atualizado);
    }

    private void liberarAutomovel(Pedido pedido) {
        Automovel automovel = pedido.getAutomovel();
        automovel.setDisponivel(true);
        automovelRepository.update(automovel);
    }
}
