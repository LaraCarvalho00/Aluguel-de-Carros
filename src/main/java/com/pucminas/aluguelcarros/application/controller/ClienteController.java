package com.pucminas.aluguelcarros.application.controller;

import com.pucminas.aluguelcarros.application.dto.request.ClienteRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ClienteResponseDTO;
import com.pucminas.aluguelcarros.application.handler.ErrorResponse;
import com.pucminas.aluguelcarros.application.service.IClienteService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

@Controller("/api/v1/clientes")
@Validated
@Tag(name = "Clientes", description = "Operações de CRUD para a entidade Cliente")
public class ClienteController {

    private final IClienteService clienteService;

    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Post
    @Status(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar cliente", description = "Cria um novo cliente no sistema")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/RG duplicado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ClienteResponseDTO criar(@Body @Valid ClienteRequestDTO dto) {
        return clienteService.criar(dto);
    }

    @Get
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    public List<ClienteResponseDTO> listarTodos() {
        return clienteService.listarTodos();
    }

    @Get("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna um cliente pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ClienteResponseDTO buscarPorId(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @Put("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/RG duplicado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ClienteResponseDTO atualizar(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Body @Valid ClienteRequestDTO dto) {
        return clienteService.atualizar(id, dto);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover cliente", description = "Remove um cliente pelo ID")
    @ApiResponse(responseCode = "204", description = "Cliente removido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public void deletar(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        clienteService.deletar(id);
    }

    @Get("/cpf/{cpf}")
    @Operation(summary = "Buscar por CPF", description = "Retorna um cliente pelo seu CPF")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ClienteResponseDTO buscarPorCpf(
            @Parameter(description = "CPF do cliente (11 a 14 caracteres)") @PathVariable String cpf) {
        return clienteService.buscarPorCpf(cpf);
    }
}
