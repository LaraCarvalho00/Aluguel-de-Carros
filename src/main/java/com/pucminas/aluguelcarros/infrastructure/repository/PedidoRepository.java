package com.pucminas.aluguelcarros.infrastructure.repository;

import com.pucminas.aluguelcarros.domain.entity.Pedido;
import com.pucminas.aluguelcarros.domain.enums.StatusPedido;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@Repository
public interface PedidoRepository extends CrudRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByAutomovelId(Long automovelId);
}
