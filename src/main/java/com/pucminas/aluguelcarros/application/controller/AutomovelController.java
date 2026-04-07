package com.pucminas.aluguelcarros.application.controller;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.application.handler.ErrorResponse;
import com.pucminas.aluguelcarros.application.service.IAutomovelService;
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

@Controller("/api/v1/automoveis")
@Validated
@Tag(name = "Automóveis", description = "Operações de CRUD para a entidade Automóvel")
public class AutomovelController {

    private final IAutomovelService automovelService;

    public AutomovelController(IAutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @Post
    @Status(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar automóvel", description = "Registra um novo automóvel na frota")
    @ApiResponse(responseCode = "201", description = "Automóvel criado com sucesso",
            content = @Content(schema = @Schema(implementation = AutomovelResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou placa/matrícula duplicada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public AutomovelResponseDTO criar(@Body @Valid AutomovelRequestDTO dto) {
        return automovelService.criar(dto);
    }

    @Get
    @Operation(summary = "Listar automóveis", description = "Retorna todos os automóveis cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de automóveis")
    public List<AutomovelResponseDTO> listarTodos() {
        return automovelService.listarTodos();
    }

    @Get("/disponiveis")
    @Operation(summary = "Listar disponíveis", description = "Retorna automóveis disponíveis para aluguel")
    @ApiResponse(responseCode = "200", description = "Lista de automóveis disponíveis")
    public List<AutomovelResponseDTO> listarDisponiveis() {
        return automovelService.listarDisponiveis();
    }

    @Get("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna um automóvel pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Automóvel encontrado",
            content = @Content(schema = @Schema(implementation = AutomovelResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Automóvel não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public AutomovelResponseDTO buscarPorId(
            @Parameter(description = "ID do automóvel") @PathVariable Long id) {
        return automovelService.buscarPorId(id);
    }

    @Put("/{id}")
    @Operation(summary = "Atualizar automóvel", description = "Atualiza os dados de um automóvel")
    @ApiResponse(responseCode = "200", description = "Automóvel atualizado",
            content = @Content(schema = @Schema(implementation = AutomovelResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Automóvel não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public AutomovelResponseDTO atualizar(
            @Parameter(description = "ID do automóvel") @PathVariable Long id,
            @Body @Valid AutomovelRequestDTO dto) {
        return automovelService.atualizar(id, dto);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover automóvel", description = "Remove um automóvel pelo ID")
    @ApiResponse(responseCode = "204", description = "Automóvel removido")
    @ApiResponse(responseCode = "404", description = "Automóvel não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public void deletar(
            @Parameter(description = "ID do automóvel") @PathVariable Long id) {
        automovelService.deletar(id);
    }
}
