package com.pucminas.aluguelcarros.unit;

import com.pucminas.aluguelcarros.application.dto.request.ContratoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ContratoResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.ContratoMapper;
import com.pucminas.aluguelcarros.application.service.impl.ContratoServiceImpl;
import com.pucminas.aluguelcarros.domain.entity.Automovel;
import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.domain.entity.Contrato;
import com.pucminas.aluguelcarros.domain.entity.Pedido;
import com.pucminas.aluguelcarros.domain.enums.StatusPedido;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.ContratoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContratoServiceImplTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ContratoMapper contratoMapper;

    @InjectMocks
    private ContratoServiceImpl contratoService;

    private Pedido pedido;
    private Contrato contrato;
    private ContratoRequestDTO requestDTO;
    private ContratoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        Automovel automovel = new Automovel();
        automovel.setId(1L);
        automovel.setMarca("Toyota");
        automovel.setModelo("Corolla");
        automovel.setAno(2024);
        automovel.setPlaca("ABC-1234");

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setStatus(StatusPedido.APROVADO);
        pedido.setDataInicio(LocalDate.of(2026, 4, 10));
        pedido.setDataFim(LocalDate.of(2026, 4, 20));

        contrato = new Contrato();
        contrato.setId(1L);
        contrato.setPedido(pedido);
        contrato.setValorTotal(new BigDecimal("5000.00"));
        contrato.setTaxaJuros(new BigDecimal("1.50"));
        contrato.setParcelas(12);
        contrato.setBancoAgente("Banco do Brasil");
        contrato.setDataCriacao(LocalDateTime.now());

        requestDTO = new ContratoRequestDTO(
                1L, new BigDecimal("5000.00"), new BigDecimal("1.50"), 12, "Banco do Brasil"
        );

        responseDTO = new ContratoResponseDTO(
                1L, 1L, "João Silva", "Toyota Corolla 2024 - ABC-1234",
                new BigDecimal("5000.00"), new BigDecimal("1.50"), 12,
                "Banco do Brasil", LocalDateTime.now().toString()
        );
    }

    @Test
    @DisplayName("Deve criar contrato para pedido aprovado")
    void deveCriarContratoParaPedidoAprovado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(contratoRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        when(pedidoRepository.update(any(Pedido.class))).thenReturn(pedido);
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponseDTO(contrato)).thenReturn(responseDTO);

        ContratoResponseDTO resultado = contratoService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Banco do Brasil", resultado.bancoAgente());
        assertEquals(12, resultado.parcelas());
        verify(contratoRepository).save(any(Contrato.class));
        verify(pedidoRepository).update(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar contrato para pedido nao aprovado")
    void deveLancarExcecaoQuandoPedidoNaoAprovado() {
        pedido.setStatus(StatusPedido.PENDENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> contratoService.criar(requestDTO));
        verify(contratoRepository, never()).save(any(Contrato.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar contrato duplicado para mesmo pedido")
    void deveLancarExcecaoQuandoContratoDuplicado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(contratoRepository.findByPedidoId(1L)).thenReturn(Optional.of(contrato));

        assertThrows(BusinessException.class, () -> contratoService.criar(requestDTO));
        verify(contratoRepository, never()).save(any(Contrato.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar contrato para pedido inexistente")
    void deveLancarExcecaoQuandoPedidoInexistente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contratoService.criar(requestDTO));
    }

    @Test
    @DisplayName("Deve buscar contrato por ID com sucesso")
    void deveBuscarContratoPorIdComSucesso() {
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
        when(contratoMapper.toResponseDTO(contrato)).thenReturn(responseDTO);

        ContratoResponseDTO resultado = contratoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    @DisplayName("Deve lancar excecao quando contrato nao encontrado por ID")
    void deveLancarExcecaoQuandoContratoNaoEncontradoPorId() {
        when(contratoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contratoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar contrato por pedido ID com sucesso")
    void deveBuscarContratoPorPedidoComSucesso() {
        when(contratoRepository.findByPedidoId(1L)).thenReturn(Optional.of(contrato));
        when(contratoMapper.toResponseDTO(contrato)).thenReturn(responseDTO);

        ContratoResponseDTO resultado = contratoService.buscarPorPedido(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.pedidoId());
    }
}
