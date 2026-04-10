package com.pucminas.aluguelcarros.application.controller;

import com.pucminas.aluguelcarros.application.dto.request.ContratoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.ContratoResponseDTO;
import com.pucminas.aluguelcarros.application.facade.AluguelFacade;
import com.pucminas.aluguelcarros.application.handler.ErrorResponse;
import com.pucminas.aluguelcarros.application.service.IContratoService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

@Controller("/api/v1/contratos")
@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Contratos", description = "Operações de gestão de contratos de crédito")
public class ContratoController {

    private final AluguelFacade aluguelFacade;
    private final IContratoService contratoService;

    public ContratoController(AluguelFacade aluguelFacade, IContratoService contratoService) {
        this.aluguelFacade = aluguelFacade;
        this.contratoService = contratoService;
    }

    @Post
    @Status(HttpStatus.CREATED)
    @Secured({"AGENTE", "ADMIN"})
    @Operation(summary = "Executar contrato", description = "Cria um contrato de crédito para pedido aprovado")
    @ApiResponse(responseCode = "201", description = "Contrato criado com sucesso",
            content = @Content(schema = @Schema(implementation = ContratoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Pedido não aprovado ou contrato já existente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ContratoResponseDTO criar(@Body @Valid ContratoRequestDTO dto) {
        return aluguelFacade.executarContrato(dto);
    }

    @Get
    @Operation(summary = "Listar contratos", description = "Retorna todos os contratos")
    @ApiResponse(responseCode = "200", description = "Lista de contratos")
    public List<ContratoResponseDTO> listarTodos() {
        return contratoService.listarTodos();
    }

    @Get("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna um contrato pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Contrato encontrado",
            content = @Content(schema = @Schema(implementation = ContratoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Contrato não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ContratoResponseDTO buscarPorId(
            @Parameter(description = "ID do contrato") @PathVariable Long id) {
        return contratoService.buscarPorId(id);
    }

    @Get("/pedido/{pedidoId}")
    @Operation(summary = "Buscar por pedido", description = "Retorna o contrato associado a um pedido")
    @ApiResponse(responseCode = "200", description = "Contrato encontrado",
            content = @Content(schema = @Schema(implementation = ContratoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Contrato não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ContratoResponseDTO buscarPorPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long pedidoId) {
        return aluguelFacade.consultarContratoPedido(pedidoId);
    }
}
