package com.pucminas.aluguelcarros.application.mapper;

import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import com.pucminas.aluguelcarros.domain.entity.Cliente;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setRg(dto.rg());
        cliente.setCpf(dto.cpf());
        cliente.setNome(dto.nome());
        cliente.setEndereco(dto.endereco());
        cliente.setProfissao(dto.profissao());
        cliente.setEntidadesEmpregadoras(
                dto.entidadesEmpregadoras() != null ? new ArrayList<>(dto.entidadesEmpregadoras()) : new ArrayList<>()
        );
        cliente.setRendimentos(dto.rendimentos());
        return cliente;
    }

    public ClienteResponseDTO toResponseDTO(Cliente entity) {
        return new ClienteResponseDTO(
                entity.getId(),
                entity.getRg(),
                entity.getCpf(),
                entity.getNome(),
                entity.getEndereco(),
                entity.getProfissao(),
                entity.getEntidadesEmpregadoras() != null
                        ? Collections.unmodifiableList(entity.getEntidadesEmpregadoras())
                        : List.of(),
                entity.getRendimentos()
        );
    }

    public void updateEntityFromDTO(ClienteRequestDTO dto, Cliente entity) {
        entity.setRg(dto.rg());
        entity.setCpf(dto.cpf());
        entity.setNome(dto.nome());
        entity.setEndereco(dto.endereco());
        entity.setProfissao(dto.profissao());
        entity.setEntidadesEmpregadoras(
                dto.entidadesEmpregadoras() != null ? new ArrayList<>(dto.entidadesEmpregadoras()) : new ArrayList<>()
        );
        entity.setRendimentos(dto.rendimentos());
    }
}
