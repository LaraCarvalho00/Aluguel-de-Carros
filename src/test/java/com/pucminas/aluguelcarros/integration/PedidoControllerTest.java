package com.pucminas.aluguelcarros.integration;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.ParecerRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.PedidoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import com.pucminas.aluguelcarros.infrastructure.repository.AutomovelRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.ContratoRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.PedidoRepository;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
class PedidoControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    PedidoRepository pedidoRepository;

    @Inject
    ContratoRepository contratoRepository;

    @Inject
    AutomovelRepository automovelRepository;

    @Inject
    ClienteRepository clienteRepository;

    private Long clienteId;
    private Long automovelId;

    @BeforeEach
    void setUp() {
        contratoRepository.deleteAll();
        pedidoRepository.deleteAll();
        automovelRepository.deleteAll();
        clienteRepository.deleteAll();

        ClienteRequestDTO clienteDTO = new ClienteRequestDTO(
                "MG-12.345.678", "12345678901", "João Silva",
                "Rua das Flores, 123", "Engenheiro",
                List.of("Empresa A"), new BigDecimal("8500.00")
        );
        HttpResponse<ClienteResponseDTO> clienteResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/clientes", clienteDTO),
                ClienteResponseDTO.class
        );
        clienteId = clienteResp.body().id();

        AutomovelRequestDTO automovelDTO = new AutomovelRequestDTO(
                "MAT-001", 2024, "Toyota", "Corolla", "ABC-1234", "EMPRESA"
        );
        HttpResponse<AutomovelResponseDTO> automovelResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/automoveis", automovelDTO),
                AutomovelResponseDTO.class
        );
        automovelId = automovelResp.body().id();
    }

    private PedidoRequestDTO criarPedidoDTO() {
        return new PedidoRequestDTO(clienteId, automovelId, "2026-05-01", "2026-05-15");
    }

    @Test
    @DisplayName("POST /api/v1/pedidos - Deve criar pedido e retornar 201")
    void deveCriarPedido() {
        HttpResponse<PedidoResponseDTO> response = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatus());
        PedidoResponseDTO body = response.body();
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals("PENDENTE", body.status());
        assertEquals("João Silva", body.clienteNome());
    }

    @Test
    @DisplayName("GET /api/v1/pedidos - Deve listar todos os pedidos")
    void deveListarTodosOsPedidos() {
        client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );

        List<PedidoResponseDTO> lista = client.toBlocking().retrieve(
                HttpRequest.GET("/api/v1/pedidos"),
                Argument.listOf(PedidoResponseDTO.class)
        );

        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("GET /api/v1/pedidos/{id} - Deve buscar pedido por ID")
    void deveBuscarPedidoPorId() {
        HttpResponse<PedidoResponseDTO> criadoResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );
        Long pedidoId = criadoResp.body().id();

        PedidoResponseDTO encontrado = client.toBlocking().retrieve(
                HttpRequest.GET("/api/v1/pedidos/" + pedidoId),
                PedidoResponseDTO.class
        );

        assertNotNull(encontrado);
        assertEquals(pedidoId, encontrado.id());
    }

    @Test
    @DisplayName("GET /api/v1/pedidos/cliente/{clienteId} - Deve listar pedidos por cliente")
    void deveListarPedidosPorCliente() {
        client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );

        List<PedidoResponseDTO> lista = client.toBlocking().retrieve(
                HttpRequest.GET("/api/v1/pedidos/cliente/" + clienteId),
                Argument.listOf(PedidoResponseDTO.class)
        );

        assertEquals(1, lista.size());
        assertEquals(clienteId, lista.get(0).clienteId());
    }

    @Test
    @DisplayName("PATCH /api/v1/pedidos/{id}/avaliar - Deve aprovar pedido")
    void deveAprovarPedido() {
        HttpResponse<PedidoResponseDTO> criadoResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );
        Long pedidoId = criadoResp.body().id();

        ParecerRequestDTO parecer = new ParecerRequestDTO(true, "Cliente com bom historico financeiro");

        PedidoResponseDTO aprovado = client.toBlocking().retrieve(
                HttpRequest.PATCH("/api/v1/pedidos/" + pedidoId + "/avaliar", parecer),
                PedidoResponseDTO.class
        );

        assertEquals("APROVADO", aprovado.status());
        assertEquals("Cliente com bom historico financeiro", aprovado.parecer());
    }

    @Test
    @DisplayName("PATCH /api/v1/pedidos/{id}/avaliar - Deve reprovar pedido")
    void deveReprovarPedido() {
        HttpResponse<PedidoResponseDTO> criadoResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );
        Long pedidoId = criadoResp.body().id();

        ParecerRequestDTO parecer = new ParecerRequestDTO(false, "Rendimento insuficiente");

        PedidoResponseDTO reprovado = client.toBlocking().retrieve(
                HttpRequest.PATCH("/api/v1/pedidos/" + pedidoId + "/avaliar", parecer),
                PedidoResponseDTO.class
        );

        assertEquals("REPROVADO", reprovado.status());
        assertEquals("Rendimento insuficiente", reprovado.parecer());
    }

    @Test
    @DisplayName("PATCH /api/v1/pedidos/{id}/cancelar - Deve cancelar pedido")
    void deveCancelarPedido() {
        HttpResponse<PedidoResponseDTO> criadoResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );
        Long pedidoId = criadoResp.body().id();

        PedidoResponseDTO cancelado = client.toBlocking().retrieve(
                HttpRequest.PATCH("/api/v1/pedidos/" + pedidoId + "/cancelar", "{}"),
                PedidoResponseDTO.class
        );

        assertEquals("CANCELADO", cancelado.status());
    }

    @Test
    @DisplayName("POST /api/v1/pedidos - Deve retornar 400 para automovel indisponivel")
    void deveRetornar400ParaAutomovelIndisponivel() {
        client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/pedidos", criarPedidoDTO()),
                PedidoResponseDTO.class
        );

        AutomovelRequestDTO outroAutoDTO = new AutomovelRequestDTO(
                "MAT-002", 2023, "Honda", "Civic", "DEF-5678", "EMPRESA"
        );
        HttpResponse<AutomovelResponseDTO> outroAutoResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/automoveis", outroAutoDTO),
                AutomovelResponseDTO.class
        );

        ClienteRequestDTO outroClienteDTO = new ClienteRequestDTO(
                "SP-98.765.432", "98765432100", "Maria Santos",
                "Av Brasil, 456", "Medica",
                List.of("Hospital X"), new BigDecimal("15000.00")
        );
        HttpResponse<ClienteResponseDTO> outroClienteResp = client.toBlocking().exchange(
                HttpRequest.POST("/api/v1/clientes", outroClienteDTO),
                ClienteResponseDTO.class
        );

        PedidoRequestDTO pedidoDuplicado = new PedidoRequestDTO(
                outroClienteResp.body().id(), automovelId, "2026-06-01", "2026-06-15"
        );

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(
                        HttpRequest.POST("/api/v1/pedidos", pedidoDuplicado),
                        PedidoResponseDTO.class
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("POST /api/v1/pedidos - Deve retornar 400 para data fim antes de data inicio")
    void deveRetornar400ParaDataInvalida() {
        PedidoRequestDTO dtoInvalido = new PedidoRequestDTO(
                clienteId, automovelId, "2026-05-15", "2026-05-01"
        );

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(
                        HttpRequest.POST("/api/v1/pedidos", dtoInvalido),
                        PedidoResponseDTO.class
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
