package com.pucminas.aluguelcarros.application.service;

import com.pucminas.aluguelcarros.application.dto.request.ParecerRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.PedidoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import java.util.List;

public interface IPedidoService {

    PedidoResponseDTO criar(PedidoRequestDTO dto);

    PedidoResponseDTO buscarPorId(Long id);

    List<PedidoResponseDTO> listarTodos();

    List<PedidoResponseDTO> listarPorCliente(Long clienteId);

    PedidoResponseDTO avaliar(Long id, ParecerRequestDTO dto);

    PedidoResponseDTO cancelar(Long id);

    PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto);
}
