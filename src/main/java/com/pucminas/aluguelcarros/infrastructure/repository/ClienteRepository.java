package com.pucminas.aluguelcarros.infrastructure.repository;

import com.pucminas.aluguelcarros.domain.entity.Cliente;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByRg(String rg);

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);
}
