package com.pucminas.aluguelcarros.application.service;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import java.util.List;

public interface IAutomovelService {

    AutomovelResponseDTO criar(AutomovelRequestDTO dto);

    AutomovelResponseDTO buscarPorId(Long id);

    List<AutomovelResponseDTO> listarTodos();

    List<AutomovelResponseDTO> listarDisponiveis();

    AutomovelResponseDTO atualizar(Long id, AutomovelRequestDTO dto);

    void deletar(Long id);
}
