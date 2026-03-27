package com.pucminas.aluguelcarros.integration;

import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
class ClienteRepositoryTest {

    @Inject
    ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    private Cliente criarCliente(String rg, String cpf, String nome) {
        Cliente cliente = new Cliente();
        cliente.setRg(rg);
        cliente.setCpf(cpf);
        cliente.setNome(nome);
        cliente.setEndereco("Rua Teste, 100");
        cliente.setProfissao("Tester");
        cliente.setEntidadesEmpregadoras(List.of("Empresa Teste"));
        cliente.setRendimentos(new BigDecimal("5000.00"));
        return cliente;
    }

    @Test
    @DisplayName("Deve salvar e recuperar cliente por ID")
    void deveSalvarERecuperarCliente() {
        Cliente cliente = criarCliente("MG-11.111.111", "11111111111", "Cliente Teste");
        Cliente salvo = clienteRepository.save(cliente);

        assertNotNull(salvo.getId());

        Optional<Cliente> encontrado = clienteRepository.findById(salvo.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Cliente Teste", encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF")
    void deveBuscarClientePorCpf() {
        Cliente cliente = criarCliente("MG-22.222.222", "22222222222", "Maria CPF");
        clienteRepository.save(cliente);

        Optional<Cliente> encontrado = clienteRepository.findByCpf("22222222222");

        assertTrue(encontrado.isPresent());
        assertEquals("Maria CPF", encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve buscar cliente por RG")
    void deveBuscarClientePorRg() {
        Cliente cliente = criarCliente("MG-33.333.333", "33333333333", "Pedro RG");
        clienteRepository.save(cliente);

        Optional<Cliente> encontrado = clienteRepository.findByRg("MG-33.333.333");

        assertTrue(encontrado.isPresent());
        assertEquals("Pedro RG", encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve verificar existência por CPF")
    void deveVerificarExistenciaPorCpf() {
        Cliente cliente = criarCliente("MG-44.444.444", "44444444444", "Ana Existe");
        clienteRepository.save(cliente);

        assertTrue(clienteRepository.existsByCpf("44444444444"));
        assertFalse(clienteRepository.existsByCpf("99999999999"));
    }

    @Test
    @DisplayName("Deve verificar existência por RG")
    void deveVerificarExistenciaPorRg() {
        Cliente cliente = criarCliente("MG-55.555.555", "55555555555", "Carlos RG");
        clienteRepository.save(cliente);

        assertTrue(clienteRepository.existsByRg("MG-55.555.555"));
        assertFalse(clienteRepository.existsByRg("SP-00.000.000"));
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosOsClientes() {
        clienteRepository.save(criarCliente("MG-66.666.666", "66666666666", "Cliente 1"));
        clienteRepository.save(criarCliente("MG-77.777.777", "77777777777", "Cliente 2"));

        Iterable<Cliente> todos = clienteRepository.findAll();
        long count = clienteRepository.count();

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve deletar cliente por ID")
    void deveDeletarClientePorId() {
        Cliente cliente = criarCliente("MG-88.888.888", "88888888888", "Para Deletar");
        Cliente salvo = clienteRepository.save(cliente);

        clienteRepository.deleteById(salvo.getId());

        assertFalse(clienteRepository.findById(salvo.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve atualizar cliente")
    void deveAtualizarCliente() {
        Cliente cliente = criarCliente("MG-99.999.999", "99999999999", "Nome Original");
        Cliente salvo = clienteRepository.save(cliente);

        salvo.setNome("Nome Atualizado");
        salvo.setRendimentos(new BigDecimal("10000.00"));
        clienteRepository.update(salvo);

        Optional<Cliente> atualizado = clienteRepository.findById(salvo.getId());
        assertTrue(atualizado.isPresent());
        assertEquals("Nome Atualizado", atualizado.get().getNome());
        assertEquals(0, new BigDecimal("10000.00").compareTo(atualizado.get().getRendimentos()));
    }
}
