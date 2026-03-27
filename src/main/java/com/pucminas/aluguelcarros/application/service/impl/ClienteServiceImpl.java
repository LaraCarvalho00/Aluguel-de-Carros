package com.pucminas.aluguelcarros.application.service.impl;

import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.ClienteMapper;
import com.pucminas.aluguelcarros.application.service.IClienteService;
import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.StreamSupport;

@Singleton
public class ClienteServiceImpl implements IClienteService {

    private static final int MAX_ENTIDADES_EMPREGADORAS = 3;

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        validarEntidadesEmpregadoras(dto);
        validarCpfUnico(dto.cpf(), null);
        validarRgUnico(dto.rg(), null);

        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente salvo = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(salvo);
    }

    @Override
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    public List<ClienteResponseDTO> listarTodos() {
        return StreamSupport.stream(clienteRepository.findAll().spliterator(), false)
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));

        validarEntidadesEmpregadoras(dto);
        validarCpfUnico(dto.cpf(), clienteExistente.getId());
        validarRgUnico(dto.rg(), clienteExistente.getId());

        clienteMapper.updateEntityFromDTO(dto, clienteExistente);
        Cliente atualizado = clienteRepository.update(clienteExistente);
        return clienteMapper.toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "CPF " + cpf));
        return clienteMapper.toResponseDTO(cliente);
    }

    private void validarEntidadesEmpregadoras(ClienteRequestDTO dto) {
        if (dto.entidadesEmpregadoras() != null && dto.entidadesEmpregadoras().size() > MAX_ENTIDADES_EMPREGADORAS) {
            throw new BusinessException(
                    String.format("Máximo de %d entidades empregadoras permitidas", MAX_ENTIDADES_EMPREGADORAS)
            );
        }
    }

    private void validarCpfUnico(String cpf, Long idAtual) {
        clienteRepository.findByCpf(cpf).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new BusinessException("Já existe um cliente cadastrado com o CPF: " + cpf);
            }
        });
    }

    private void validarRgUnico(String rg, Long idAtual) {
        clienteRepository.findByRg(rg).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new BusinessException("Já existe um cliente cadastrado com o RG: " + rg);
            }
        });
    }
}
