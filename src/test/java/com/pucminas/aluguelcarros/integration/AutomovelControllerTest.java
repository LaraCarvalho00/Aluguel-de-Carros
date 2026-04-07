package com.pucminas.aluguelcarros.integration;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.infrastructure.repository.AutomovelRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
class AutomovelControllerTest {

    @Inject
    @Client("/api/v1/automoveis")
    HttpClient client;

    @Inject
    AutomovelRepository automovelRepository;

    @BeforeEach
    void setUp() {
        automovelRepository.deleteAll();
    }

    private AutomovelRequestDTO criarRequestDTO() {
        return new AutomovelRequestDTO(
                "MAT-001", 2024, "Toyota", "Corolla", "ABC-1234", "EMPRESA"
        );
    }

    @Test
    @DisplayName("POST /api/v1/automoveis - Deve criar automovel e retornar 201")
    void deveCriarAutomovel() {
        HttpResponse<AutomovelResponseDTO> response = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                AutomovelResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatus());
        AutomovelResponseDTO body = response.body();
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals("Toyota", body.marca());
        assertEquals("Corolla", body.modelo());
        assertEquals("ABC-1234", body.placa());
        assertTrue(body.disponivel());
    }

    @Test
    @DisplayName("GET /api/v1/automoveis - Deve listar todos os automoveis")
    void deveListarTodosOsAutomoveis() {
        client.toBlocking().exchange(HttpRequest.POST("/", criarRequestDTO()), AutomovelResponseDTO.class);

        AutomovelRequestDTO segundo = new AutomovelRequestDTO(
                "MAT-002", 2023, "Honda", "Civic", "DEF-5678", "EMPRESA"
        );
        client.toBlocking().exchange(HttpRequest.POST("/", segundo), AutomovelResponseDTO.class);

        List<AutomovelResponseDTO> lista = client.toBlocking().retrieve(
                HttpRequest.GET("/"),
                Argument.listOf(AutomovelResponseDTO.class)
        );

        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("GET /api/v1/automoveis/disponiveis - Deve listar apenas disponiveis")
    void deveListarApenasDisponiveis() {
        client.toBlocking().exchange(HttpRequest.POST("/", criarRequestDTO()), AutomovelResponseDTO.class);

        List<AutomovelResponseDTO> lista = client.toBlocking().retrieve(
                HttpRequest.GET("/disponiveis"),
                Argument.listOf(AutomovelResponseDTO.class)
        );

        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(AutomovelResponseDTO::disponivel));
    }

    @Test
    @DisplayName("GET /api/v1/automoveis/{id} - Deve buscar automovel por ID")
    void deveBuscarAutomovelPorId() {
        HttpResponse<AutomovelResponseDTO> criadoResponse = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                AutomovelResponseDTO.class
        );
        Long id = criadoResponse.body().id();

        AutomovelResponseDTO encontrado = client.toBlocking().retrieve(
                HttpRequest.GET("/" + id),
                AutomovelResponseDTO.class
        );

        assertNotNull(encontrado);
        assertEquals(id, encontrado.id());
        assertEquals("Toyota", encontrado.marca());
    }

    @Test
    @DisplayName("GET /api/v1/automoveis/{id} - Deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() {
        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().retrieve(HttpRequest.GET("/99999"))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("PUT /api/v1/automoveis/{id} - Deve atualizar automovel")
    void deveAtualizarAutomovel() {
        HttpResponse<AutomovelResponseDTO> criadoResponse = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                AutomovelResponseDTO.class
        );
        Long id = criadoResponse.body().id();

        AutomovelRequestDTO dtoAtualizado = new AutomovelRequestDTO(
                "MAT-001", 2025, "Toyota", "Corolla Cross", "ABC-1234", "CLIENTE"
        );

        AutomovelResponseDTO atualizado = client.toBlocking().retrieve(
                HttpRequest.PUT("/" + id, dtoAtualizado),
                AutomovelResponseDTO.class
        );

        assertEquals("Corolla Cross", atualizado.modelo());
        assertEquals(2025, atualizado.ano());
        assertEquals("CLIENTE", atualizado.proprietario());
    }

    @Test
    @DisplayName("DELETE /api/v1/automoveis/{id} - Deve deletar automovel e retornar 204")
    void deveDeletarAutomovel() {
        HttpResponse<AutomovelResponseDTO> criadoResponse = client.toBlocking().exchange(
                HttpRequest.POST("/", criarRequestDTO()),
                AutomovelResponseDTO.class
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
    @DisplayName("POST /api/v1/automoveis - Deve retornar 400 para placa duplicada")
    void deveRetornar400ParaPlacaDuplicada() {
        client.toBlocking().exchange(HttpRequest.POST("/", criarRequestDTO()), AutomovelResponseDTO.class);

        AutomovelRequestDTO duplicado = new AutomovelRequestDTO(
                "MAT-099", 2023, "Honda", "Fit", "ABC-1234", "EMPRESA"
        );

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/", duplicado), AutomovelResponseDTO.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
