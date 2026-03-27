package com.pucminas.aluguelcarros.unit;

import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.ClienteMapper;
import com.pucminas.aluguelcarros.application.service.impl.ClienteServiceImpl;
import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDTO requestDTO;
    private Cliente cliente;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = new ClienteRequestDTO(
                "MG-12.345.678",
                "12345678901",
                "João Silva",
                "Rua das Flores, 123",
                "Engenheiro",
                List.of("Empresa A", "Empresa B"),
                new BigDecimal("8500.00")
        );

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setRg("MG-12.345.678");
        cliente.setCpf("12345678901");
        cliente.setNome("João Silva");
        cliente.setEndereco("Rua das Flores, 123");
        cliente.setProfissao("Engenheiro");
        cliente.setEntidadesEmpregadoras(List.of("Empresa A", "Empresa B"));
        cliente.setRendimentos(new BigDecimal("8500.00"));

        responseDTO = new ClienteResponseDTO(
                1L,
                "MG-12.345.678",
                "12345678901",
                "João Silva",
                "Rua das Flores, 123",
                "Engenheiro",
                List.of("Empresa A", "Empresa B"),
                new BigDecimal("8500.00")
        );
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void deveCriarClienteComSucesso() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByRg(anyString())).thenReturn(Optional.empty());
        when(clienteMapper.toEntity(requestDTO)).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nome());
        assertEquals("12345678901", resultado.cpf());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com CPF duplicado")
    void deveLancarExcecaoQuandoCpfDuplicado() {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));

        assertThrows(BusinessException.class, () -> clienteService.criar(requestDTO));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com RG duplicado")
    void deveLancarExcecaoQuandoRgDuplicado() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByRg("MG-12.345.678")).thenReturn(Optional.of(cliente));

        assertThrows(BusinessException.class, () -> clienteService.criar(requestDTO));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com mais de 3 entidades empregadoras")
    void deveLancarExcecaoQuandoMaisDe3EntidadesEmpregadoras() {
        ClienteRequestDTO dtoInvalido = new ClienteRequestDTO(
                "MG-12.345.678",
                "12345678901",
                "João Silva",
                "Rua das Flores, 123",
                "Engenheiro",
                List.of("Empresa A", "Empresa B", "Empresa C", "Empresa D"),
                new BigDecimal("8500.00")
        );

        assertThrows(BusinessException.class, () -> clienteService.criar(dtoInvalido));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("João Silva", resultado.nome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado por ID")
    void deveLancarExcecaoQuandoClienteNaoEncontradoPorId() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clienteService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosOsClientes() {
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Maria Santos");

        ClienteResponseDTO response2 = new ClienteResponseDTO(
                2L, "MG-98.765.432", "98765432100", "Maria Santos",
                "Av Brasil, 456", "Médica", List.of("Hospital X"), new BigDecimal("15000.00")
        );

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente, cliente2));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);
        when(clienteMapper.toResponseDTO(cliente2)).thenReturn(response2);

        List<ClienteResponseDTO> resultado = clienteService.listarTodos();

        assertEquals(2, resultado.size());
        verify(clienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        ClienteRequestDTO dtoAtualizado = new ClienteRequestDTO(
                "MG-12.345.678",
                "12345678901",
                "João Silva Atualizado",
                "Nova Rua, 456",
                "Engenheiro Sênior",
                List.of("Empresa A"),
                new BigDecimal("12000.00")
        );

        ClienteResponseDTO responseAtualizado = new ClienteResponseDTO(
                1L, "MG-12.345.678", "12345678901", "João Silva Atualizado",
                "Nova Rua, 456", "Engenheiro Sênior", List.of("Empresa A"), new BigDecimal("12000.00")
        );

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByRg("MG-12.345.678")).thenReturn(Optional.of(cliente));
        when(clienteRepository.update(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(any(Cliente.class))).thenReturn(responseAtualizado);

        ClienteResponseDTO resultado = clienteService.atualizar(1L, dtoAtualizado);

        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.nome());
        verify(clienteRepository).update(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clienteService.atualizar(99L, requestDTO));
        verify(clienteRepository, never()).update(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.deletar(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> clienteService.deletar(99L));
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCpfComSucesso() {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.buscarPorCpf("12345678901");

        assertNotNull(resultado);
        assertEquals("12345678901", resultado.cpf());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado por CPF")
    void deveLancarExcecaoQuandoClienteNaoEncontradoPorCpf() {
        when(clienteRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clienteService.buscarPorCpf("00000000000"));
    }
}
