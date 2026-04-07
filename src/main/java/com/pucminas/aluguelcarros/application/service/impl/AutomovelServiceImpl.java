package com.pucminas.aluguelcarros.application.service.impl;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.AutomovelMapper;
import com.pucminas.aluguelcarros.application.service.IAutomovelService;
import com.pucminas.aluguelcarros.domain.entity.Automovel;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.AutomovelRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.StreamSupport;

@Singleton
public class AutomovelServiceImpl implements IAutomovelService {

    private final AutomovelRepository automovelRepository;
    private final AutomovelMapper automovelMapper;

    public AutomovelServiceImpl(AutomovelRepository automovelRepository, AutomovelMapper automovelMapper) {
        this.automovelRepository = automovelRepository;
        this.automovelMapper = automovelMapper;
    }

    @Override
    @Transactional
    public AutomovelResponseDTO criar(AutomovelRequestDTO dto) {
        validarPlacaUnica(dto.placa(), null);
        validarMatriculaUnica(dto.matricula(), null);

        Automovel automovel = automovelMapper.toEntity(dto);
        Automovel salvo = automovelRepository.save(automovel);
        return automovelMapper.toResponseDTO(salvo);
    }

    @Override
    public AutomovelResponseDTO buscarPorId(Long id) {
        Automovel automovel = automovelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Automóvel", id));
        return automovelMapper.toResponseDTO(automovel);
    }

    @Override
    public List<AutomovelResponseDTO> listarTodos() {
        return StreamSupport.stream(automovelRepository.findAll().spliterator(), false)
                .map(automovelMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<AutomovelResponseDTO> listarDisponiveis() {
        return automovelRepository.findByDisponivelTrue().stream()
                .map(automovelMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public AutomovelResponseDTO atualizar(Long id, AutomovelRequestDTO dto) {
        Automovel existente = automovelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Automóvel", id));

        validarPlacaUnica(dto.placa(), existente.getId());
        validarMatriculaUnica(dto.matricula(), existente.getId());

        automovelMapper.updateEntityFromDTO(dto, existente);
        Automovel atualizado = automovelRepository.update(existente);
        return automovelMapper.toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!automovelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Automóvel", id);
        }
        automovelRepository.deleteById(id);
    }

    private void validarPlacaUnica(String placa, Long idAtual) {
        automovelRepository.findByPlaca(placa).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new BusinessException("Já existe um automóvel com a placa: " + placa);
            }
        });
    }

    private void validarMatriculaUnica(String matricula, Long idAtual) {
        automovelRepository.findByMatricula(matricula).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new BusinessException("Já existe um automóvel com a matrícula: " + matricula);
            }
        });
    }
}
