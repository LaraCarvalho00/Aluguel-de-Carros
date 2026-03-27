package com.pucminas.aluguelcarros.application.service;

import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import java.util.List;

public interface IClienteService {

    ClienteResponseDTO criar(ClienteRequestDTO dto);

    ClienteResponseDTO buscarPorId(Long id);

    List<ClienteResponseDTO> listarTodos();

    ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto);

    void deletar(Long id);

    ClienteResponseDTO buscarPorCpf(String cpf);
}
