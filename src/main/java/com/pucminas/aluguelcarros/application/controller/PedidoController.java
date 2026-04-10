package com.pucminas.aluguelcarros.application.controller;

import com.pucminas.aluguelcarros.application.dto.request.ParecerRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.PedidoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import com.pucminas.aluguelcarros.application.facade.AluguelFacade;
import com.pucminas.aluguelcarros.application.handler.ErrorResponse;
import com.pucminas.aluguelcarros.application.service.IClienteService;
import com.pucminas.aluguelcarros.application.service.IPedidoService;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
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

@Controller("/api/v1/pedidos")
@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Pedidos", description = "Operações de gestão de pedidos de aluguel")
public class PedidoController {

    private final AluguelFacade aluguelFacade;
    private final IPedidoService pedidoService;
    private final IClienteService clienteService;

    public PedidoController(AluguelFacade aluguelFacade,
                            IPedidoService pedidoService,
                            IClienteService clienteService) {
        this.aluguelFacade = aluguelFacade;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
    }

    private void verificarPropriedade(Long pedidoId, Authentication auth) {
        if (auth.getRoles().contains("CLIENTE")) {
            Long clienteIdPedido = pedidoService.buscarPorId(pedidoId).clienteId();
            Long clienteIdAutenticado = clienteService.buscarPorCpf(auth.getName()).id();
            if (!clienteIdPedido.equals(clienteIdAutenticado)) {
                throw new BusinessException("Acesso negado: este pedido não pertence a você");
            }
        }
    }

    @Post
    @Status(HttpStatus.CREATED)
    @Secured({"CLIENTE", "ADMIN"})
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido de aluguel")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou automóvel indisponível",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PedidoResponseDTO criar(@Body @Valid PedidoRequestDTO dto, Authentication authentication) {
        if (authentication.getRoles().contains("CLIENTE")) {
            Long clienteId = clienteService.buscarPorCpf(authentication.getName()).id();
            dto = new PedidoRequestDTO(clienteId, dto.automovelId(), dto.dataInicio(), dto.dataFim());
        }
        return aluguelFacade.solicitarAluguel(dto);
    }

    @Get
    @Operation(summary = "Listar pedidos", description = "Retorna todos os pedidos de aluguel")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoService.listarTodos();
    }

    @Get("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna um pedido pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PedidoResponseDTO buscarPorId(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @Get("/cliente/{clienteId}")
    @Operation(summary = "Listar por cliente", description = "Retorna todos os pedidos de um cliente")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos do cliente")
    public List<PedidoResponseDTO> listarPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        return aluguelFacade.consultarPedidosCliente(clienteId);
    }

    @Put("/{id}")
    @Secured({"CLIENTE", "ADMIN"})
    @Operation(summary = "Atualizar pedido", description = "Modifica um pedido pendente")
    @ApiResponse(responseCode = "200", description = "Pedido atualizado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Pedido não pode ser modificado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PedidoResponseDTO atualizar(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Body @Valid PedidoRequestDTO dto,
            Authentication authentication) {
        verificarPropriedade(id, authentication);
        return pedidoService.atualizar(id, dto);
    }

    @Patch("/{id}/avaliar")
    @Secured({"AGENTE", "ADMIN"})
    @Operation(summary = "Avaliar pedido", description = "Emite parecer financeiro sobre o pedido (aprovar/reprovar)")
    @ApiResponse(responseCode = "200", description = "Pedido avaliado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Pedido não pode ser avaliado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PedidoResponseDTO avaliar(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Body @Valid ParecerRequestDTO dto) {
        return aluguelFacade.avaliarPedido(id, dto);
    }

    @Patch("/{id}/cancelar")
    @Secured({"CLIENTE", "ADMIN"})
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido de aluguel")
    @ApiResponse(responseCode = "200", description = "Pedido cancelado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PedidoResponseDTO cancelar(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            Authentication authentication) {
        verificarPropriedade(id, authentication);
        return aluguelFacade.cancelarPedido(id);
    }
}
