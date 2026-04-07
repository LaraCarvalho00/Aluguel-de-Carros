package com.pucminas.aluguelcarros.infrastructure.repository;

import com.pucminas.aluguelcarros.domain.entity.Contrato;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@Repository
public interface ContratoRepository extends CrudRepository<Contrato, Long> {

    Optional<Contrato> findByPedidoId(Long pedidoId);
}
