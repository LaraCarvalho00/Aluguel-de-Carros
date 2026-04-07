package com.pucminas.aluguelcarros.unit;

import com.pucminas.aluguelcarros.application.dto.request.ParecerRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.PedidoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.PedidoMapper;
import com.pucminas.aluguelcarros.application.service.impl.PedidoServiceImpl;
import com.pucminas.aluguelcarros.domain.entity.Automovel;
import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.domain.entity.Pedido;
import com.pucminas.aluguelcarros.domain.enums.StatusPedido;
import com.pucminas.aluguelcarros.domain.enums.TipoPropriedade;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.AutomovelRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AutomovelRepository automovelRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente cliente;
    private Automovel automovel;
    private Pedido pedido;
    private PedidoRequestDTO requestDTO;
    private PedidoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setRg("MG-12.345.678");
        cliente.setEndereco("Rua Teste");
        cliente.setProfissao("Engenheiro");
        cliente.setRendimentos(new BigDecimal("8500.00"));

        automovel = new Automovel();
        automovel.setId(1L);
        automovel.setMarca("Toyota");
        automovel.setModelo("Corolla");
        automovel.setAno(2024);
        automovel.setPlaca("ABC-1234");
        automovel.setMatricula("MAT-001");
        automovel.setDisponivel(true);
        automovel.setProprietario(TipoPropriedade.EMPRESA);

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataInicio(LocalDate.of(2026, 4, 10));
        pedido.setDataFim(LocalDate.of(2026, 4, 20));
        pedido.setDataCriacao(LocalDateTime.now());

        requestDTO = new PedidoRequestDTO(1L, 1L, "2026-04-10", "2026-04-20");

        responseDTO = new PedidoResponseDTO(
                1L, 1L, "João Silva", 1L, "Toyota Corolla 2024 - ABC-1234",
                "PENDENTE", "Pendente", "2026-04-10", "2026-04-20",
                null, LocalDateTime.now().toString(), null
        );
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(automovelRepository.findById(1L)).thenReturn(Optional.of(automovel));
        when(automovelRepository.update(any(Automovel.class))).thenReturn(automovel);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(responseDTO);

        PedidoResponseDTO resultado = pedidoService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("PENDENTE", resultado.status());
        assertEquals("João Silva", resultado.clienteNome());
        verify(pedidoRepository).save(any(Pedido.class));
        verify(automovelRepository).update(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar pedido com cliente inexistente")
    void deveLancarExcecaoQuandoClienteInexistente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.criar(requestDTO));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar pedido com automovel inexistente")
    void deveLancarExcecaoQuandoAutomovelInexistente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(automovelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.criar(requestDTO));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar pedido com automovel indisponivel")
    void deveLancarExcecaoQuandoAutomovelIndisponivel() {
        automovel.setDisponivel(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(automovelRepository.findById(1L)).thenReturn(Optional.of(automovel));

        assertThrows(BusinessException.class, () -> pedidoService.criar(requestDTO));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lancar excecao quando data fim anterior a data inicio")
    void deveLancarExcecaoQuandoDataFimAnteriorADataInicio() {
        PedidoRequestDTO dtoInvalido = new PedidoRequestDTO(1L, 1L, "2026-04-20", "2026-04-10");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(automovelRepository.findById(1L)).thenReturn(Optional.of(automovel));

        assertThrows(BusinessException.class, () -> pedidoService.criar(dtoInvalido));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorIdComSucesso() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toResponseDTO(pedido)).thenReturn(responseDTO);

        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    @DisplayName("Deve lancar excecao quando pedido nao encontrado por ID")
    void deveLancarExcecaoQuandoPedidoNaoEncontradoPorId() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve listar pedidos por cliente")
    void deveListarPedidosPorCliente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));
        when(pedidoMapper.toResponseDTO(pedido)).thenReturn(responseDTO);

        List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

        assertEquals(1, resultado.size());
        verify(pedidoRepository).findByClienteId(1L);
    }

    @Test
    @DisplayName("Deve aprovar pedido com parecer positivo")
    void deveAprovarPedidoComParecerPositivo() {
        ParecerRequestDTO parecerDTO = new ParecerRequestDTO(true, "Cliente com bom historico");
        PedidoResponseDTO aprovadoDTO = new PedidoResponseDTO(
                1L, 1L, "João Silva", 1L, "Toyota Corolla 2024 - ABC-1234",
                "APROVADO", "Aprovado", "2026-04-10", "2026-04-20",
                "Cliente com bom historico", responseDTO.dataCriacao(), LocalDateTime.now().toString()
        );

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.update(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(aprovadoDTO);

        PedidoResponseDTO resultado = pedidoService.avaliar(1L, parecerDTO);

        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.status());
        assertEquals("Cliente com bom historico", resultado.parecer());
        verify(pedidoRepository).update(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve reprovar pedido e liberar automovel")
    void deveReprovarPedidoELiberarAutomovel() {
        ParecerRequestDTO parecerDTO = new ParecerRequestDTO(false, "Rendimento insuficiente");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(automovelRepository.update(any(Automovel.class))).thenReturn(automovel);
        when(pedidoRepository.update(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(responseDTO);

        pedidoService.avaliar(1L, parecerDTO);

        verify(automovelRepository).update(any(Automovel.class));
        verify(pedidoRepository).update(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao avaliar pedido ja contratado")
    void deveLancarExcecaoAoAvaliarPedidoJaContratado() {
        pedido.setStatus(StatusPedido.CONTRATADO);
        ParecerRequestDTO parecerDTO = new ParecerRequestDTO(true, "Parecer");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> pedidoService.avaliar(1L, parecerDTO));
    }

    @Test
    @DisplayName("Deve cancelar pedido e liberar automovel")
    void deveCancelarPedidoELiberarAutomovel() {
        PedidoResponseDTO canceladoDTO = new PedidoResponseDTO(
                1L, 1L, "João Silva", 1L, "Toyota Corolla 2024 - ABC-1234",
                "CANCELADO", "Cancelado", "2026-04-10", "2026-04-20",
                null, responseDTO.dataCriacao(), LocalDateTime.now().toString()
        );

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(automovelRepository.update(any(Automovel.class))).thenReturn(automovel);
        when(pedidoRepository.update(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(canceladoDTO);

        PedidoResponseDTO resultado = pedidoService.cancelar(1L);

        assertNotNull(resultado);
        assertEquals("CANCELADO", resultado.status());
        verify(automovelRepository).update(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar pedido ja contratado")
    void deveLancarExcecaoAoCancelarPedidoJaContratado() {
        pedido.setStatus(StatusPedido.CONTRATADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> pedidoService.cancelar(1L));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar pedido ja cancelado")
    void deveLancarExcecaoAoCancelarPedidoJaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> pedidoService.cancelar(1L));
    }

    @Test
    @DisplayName("Deve lancar excecao ao modificar pedido nao pendente")
    void deveLancarExcecaoAoModificarPedidoNaoPendente() {
        pedido.setStatus(StatusPedido.APROVADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> pedidoService.atualizar(1L, requestDTO));
    }
}
