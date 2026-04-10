package com.pucminas.aluguelcarros.infrastructure.repository;

import com.pucminas.aluguelcarros.domain.entity.Usuario;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
