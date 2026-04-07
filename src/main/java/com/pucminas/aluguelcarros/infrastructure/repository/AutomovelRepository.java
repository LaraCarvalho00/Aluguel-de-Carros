package com.pucminas.aluguelcarros.infrastructure.repository;

import com.pucminas.aluguelcarros.domain.entity.Automovel;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutomovelRepository extends CrudRepository<Automovel, Long> {

    Optional<Automovel> findByPlaca(String placa);

    Optional<Automovel> findByMatricula(String matricula);

    List<Automovel> findByDisponivelTrue();

    boolean existsByPlaca(String placa);

    boolean existsByMatricula(String matricula);
}
