package com.pucminas.aluguelcarros.integration;

import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
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
class ClienteControllerTest {

    @Inject
    @Client("/api/v1/clientes")
    HttpClient client;

    @Inject
    ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    private ClienteRequestDTO criarRequestDTO() {
        return new ClienteRequestDTO(
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
    @DisplayName("POST /api/v1/clientes - Deve criar cliente e retornar 201")
    void deveCriarCliente() {
        HttpResponse<ClienteResponseDTO> response = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                ClienteResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatus());
        ClienteResponseDTO body = response.body();
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals("João Silva", body.nome());
        assertEquals("12345678901", body.cpf());
    }

    @Test
    @DisplayName("GET /api/v1/clientes - Deve listar todos os clientes")
    void deveListarTodosOsClientes() {
        client.toBlocking().exchange(HttpRequest.POST("/", criarRequestDTO()), ClienteResponseDTO.class);

        ClienteRequestDTO segundoCliente = new ClienteRequestDTO(
                "SP-98.765.432",
                "98765432100",
                "Maria Santos",
                "Av Brasil, 456",
                "Médica",
                List.of("Hospital X"),
                new BigDecimal("15000.00")
        );
        client.toBlocking().exchange(HttpRequest.POST("/", segundoCliente), ClienteResponseDTO.class);

        List<ClienteResponseDTO> lista = client.toBlocking().retrieve(
                HttpRequest.GET("/"),
                Argument.listOf(ClienteResponseDTO.class)
        );

        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} - Deve buscar cliente por ID")
    void deveBuscarClientePorId() {
        HttpResponse<ClienteResponseDTO> criadoResponse = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                ClienteResponseDTO.class
        );
        Long id = criadoResponse.body().id();

        ClienteResponseDTO encontrado = client.toBlocking().retrieve(
                HttpRequest.GET("/" + id),
                ClienteResponseDTO.class
        );

        assertNotNull(encontrado);
        assertEquals(id, encontrado.id());
        assertEquals("João Silva", encontrado.nome());
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} - Deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() {
        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().retrieve(HttpRequest.GET("/99999"))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("PUT /api/v1/clientes/{id} - Deve atualizar cliente")
    void deveAtualizarCliente() {
        HttpResponse<ClienteResponseDTO> criadoResponse = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                ClienteResponseDTO.class
        );
        Long id = criadoResponse.body().id();

        ClienteRequestDTO dtoAtualizado = new ClienteRequestDTO(
                "MG-12.345.678",
                "12345678901",
                "João Silva Atualizado",
                "Nova Rua, 456",
                "Engenheiro Sênior",
                List.of("Empresa A"),
                new BigDecimal("12000.00")
        );

        ClienteResponseDTO atualizado = client.toBlocking().retrieve(
                HttpRequest.PUT("/" + id, dtoAtualizado),
                ClienteResponseDTO.class
        );

        assertEquals("João Silva Atualizado", atualizado.nome());
        assertEquals("Nova Rua, 456", atualizado.endereco());
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/{id} - Deve deletar cliente e retornar 204")
    void deveDeletarCliente() {
        HttpResponse<ClienteResponseDTO> criadoResponse = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                ClienteResponseDTO.class
        );
        Long id = criadoResponse.body().id();

        HttpResponse<?> deleteResponse = client.toBlocking().exchange(
                HttpRequest.DELETE("/" + id)
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatus());

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().retrieve(HttpRequest.GET("/" + id))
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("GET /api/v1/clientes/cpf/{cpf} - Deve buscar cliente por CPF")
    void deveBuscarClientePorCpf() {
        client.toBlocking().exchange(HttpRequest.POST("/", criarRequestDTO()), ClienteResponseDTO.class);

        ClienteResponseDTO encontrado = client.toBlocking().retrieve(
                HttpRequest.GET("/cpf/12345678901"),
                ClienteResponseDTO.class
        );

        assertNotNull(encontrado);
        assertEquals("12345678901", encontrado.cpf());
        assertEquals("João Silva", encontrado.nome());
    }

    @Test
    @DisplayName("POST /api/v1/clientes - Deve retornar 400 para CPF duplicado")
    void deveRetornar400ParaCpfDuplicado() {
        client.toBlocking().exchange(HttpRequest.POST("/", criarRequestDTO()), ClienteResponseDTO.class);

        ClienteRequestDTO duplicado = new ClienteRequestDTO(
                "SP-99.999.999",
                "12345678901",
                "Outro Nome",
                "Outro Endereço",
                "Outra Profissão",
                List.of(),
                new BigDecimal("5000.00")
        );

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/", duplicado), ClienteResponseDTO.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
