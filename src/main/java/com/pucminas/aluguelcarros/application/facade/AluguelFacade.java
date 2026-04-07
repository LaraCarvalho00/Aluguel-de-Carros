package com.pucminas.aluguelcarros.application.facade;

import com.pucminas.aluguelcarros.application.dto.request.ContratoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.ParecerRequestDTO;
import com.pucminas.aluguelcarros.application.dto.request.PedidoRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.application.dto.response.ContratoResponseDTO;
import com.pucminas.aluguelcarros.application.dto.response.PedidoResponseDTO;
import com.pucminas.aluguelcarros.application.service.IAutomovelService;
import com.pucminas.aluguelcarros.application.service.IContratoService;
import com.pucminas.aluguelcarros.application.service.IPedidoService;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class AluguelFacade {

    private final IPedidoService pedidoService;
    private final IAutomovelService automovelService;
    private final IContratoService contratoService;

    public AluguelFacade(IPedidoService pedidoService,
                         IAutomovelService automovelService,
                         IContratoService contratoService) {
        this.pedidoService = pedidoService;
        this.automovelService = automovelService;
        this.contratoService = contratoService;
    }

    public PedidoResponseDTO solicitarAluguel(PedidoRequestDTO dto) {
        return pedidoService.criar(dto);
    }

    public PedidoResponseDTO avaliarPedido(Long pedidoId, ParecerRequestDTO dto) {
        return pedidoService.avaliar(pedidoId, dto);
    }

    public ContratoResponseDTO executarContrato(ContratoRequestDTO dto) {
        return contratoService.criar(dto);
    }

    public PedidoResponseDTO cancelarPedido(Long pedidoId) {
        return pedidoService.cancelar(pedidoId);
    }

    public List<AutomovelResponseDTO> consultarAutomoveisDisponiveis() {
        return automovelService.listarDisponiveis();
    }

    public List<PedidoResponseDTO> consultarPedidosCliente(Long clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }

    public ContratoResponseDTO consultarContratoPedido(Long pedidoId) {
        return contratoService.buscarPorPedido(pedidoId);
    }
}
