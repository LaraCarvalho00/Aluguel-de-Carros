package com.pucminas.aluguelcarros.application.service;

import com.pucminas.aluguelcarros.application.dto.request.ContratoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ContratoResponseDTO;
import java.util.List;

public interface IContratoService {

    ContratoResponseDTO criar(ContratoRequestDTO dto);

    ContratoResponseDTO buscarPorId(Long id);

    ContratoResponseDTO buscarPorPedido(Long pedidoId);

    List<ContratoResponseDTO> listarTodos();
}
