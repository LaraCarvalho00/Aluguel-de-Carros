import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { FiPlus, FiInbox } from "react-icons/fi";
import toast from "react-hot-toast";
import { pedidoService, clienteService } from "../services/api";
import type { PedidoResponse } from "../types/pedido";
import PedidoCard from "../components/PedidoCard";
import ConfirmModal from "../components/ConfirmModal";
import ParecerModal from "../components/ParecerModal";
import { useAuth } from "../contexts/AuthContext";

export default function PedidoListPage() {
  const { hasRole, user } = useAuth();
  const isCliente      = hasRole("CLIENTE");
  const podeNovoPedido = isCliente || hasRole("ADMIN");
  const podeCancelar   = isCliente || hasRole("ADMIN");
  const podeAvaliar    = hasRole("AGENTE") || hasRole("ADMIN");
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [cancelId, setCancelId] = useState<number | null>(null);
  const [cancelling, setCancelling] = useState(false);
  const [avaliarId, setAvaliarId] = useState<number | null>(null);

  useEffect(() => {
    const fetchPedidos = async () => {
      try {
        setLoading(true);
        if (isCliente && user?.cpf) {
          // CLIENTE só vê os próprios pedidos
          const { data: cliente } = await clienteService.buscarPorCpf(user.cpf);
          const { data } = await pedidoService.listarPorCliente(cliente.id);
          setPedidos(data);
        } else {
          const { data } = await pedidoService.listarTodos();
          setPedidos(data);
        }
      } catch {
        toast.error("Erro ao carregar pedidos.");
      } finally {
        setLoading(false);
      }
    };
    fetchPedidos();
  }, [isCliente, user?.cpf]);

  const handleCancelar = async () => {
    if (cancelId === null) return;
    try {
      setCancelling(true);
      const { data } = await pedidoService.cancelar(cancelId);
      toast.success("Pedido cancelado com sucesso!");
      setPedidos((prev) =>
        prev.map((p) => (p.id === cancelId ? data : p))
      );
    } catch {
      toast.error("Erro ao cancelar pedido.");
    } finally {
      setCancelling(false);
      setCancelId(null);
    }
  };

  const handleParecerSubmit = async (
    aprovado: boolean,
    parecer: string
  ) => {
    if (avaliarId === null) return;
    try {
      const { data } = await pedidoService.avaliar(avaliarId, {
        aprovado,
        parecer,
      });
      toast.success(
        aprovado ? "Pedido aprovado com sucesso!" : "Pedido reprovado."
      );
      setPedidos((prev) =>
        prev.map((p) => (p.id === avaliarId ? data : p))
      );
    } catch {
      toast.error("Erro ao avaliar pedido.");
    } finally {
      setAvaliarId(null);
    }
  };

  if (loading) {
    return <div className="loading">Carregando pedidos...</div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Pedidos de Aluguel</h1>
        {podeNovoPedido && (
          <Link to="/pedidos/novo" className="btn btn-primary">
            <FiPlus size={16} /> Novo Pedido
          </Link>
        )}
      </div>

      {pedidos.length === 0 ? (
        <div className="empty-state">
          <FiInbox size={48} />
          <h2>Nenhum pedido registrado</h2>
          {podeNovoPedido && (
            <>
              <p>Crie o primeiro pedido de aluguel.</p>
              <Link to="/pedidos/novo" className="btn btn-primary">
                Criar Pedido
              </Link>
            </>
          )}
        </div>
      ) : (
        <div className="cards-grid">
          {pedidos.map((pedido) => (
            <PedidoCard
              key={pedido.id}
              pedido={pedido}
              onCancelar={podeCancelar ? setCancelId : undefined}
              onAvaliar={podeAvaliar ? setAvaliarId : undefined}
            />
          ))}
        </div>
      )}

      {cancelId !== null && (
        <ConfirmModal
          title="Cancelar Pedido"
          message="Tem certeza que deseja cancelar este pedido? O automóvel será liberado."
          onConfirm={handleCancelar}
          onCancel={() => setCancelId(null)}
          loading={cancelling}
        />
      )}

      {avaliarId !== null && (
        <ParecerModal
          onSubmit={handleParecerSubmit}
          onCancel={() => setAvaliarId(null)}
        />
      )}
    </div>
  );
}
