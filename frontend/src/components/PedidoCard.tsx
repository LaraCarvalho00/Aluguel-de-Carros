import {
  FiCalendar,
  FiTruck,
  FiUser,
  FiXCircle,
  FiCheckCircle,
  FiClock,
} from "react-icons/fi";
import type { PedidoResponse } from "../types/pedido";

interface Props {
  pedido: PedidoResponse;
  onCancelar?: (id: number) => void;
  onAvaliar?: (id: number) => void;
}

const statusConfig: Record<string, { color: string; icon: React.ReactNode }> = {
  PENDENTE: { color: "status-pendente", icon: <FiClock size={14} /> },
  EM_ANALISE: { color: "status-analise", icon: <FiClock size={14} /> },
  APROVADO: { color: "status-aprovado", icon: <FiCheckCircle size={14} /> },
  REPROVADO: { color: "status-reprovado", icon: <FiXCircle size={14} /> },
  CONTRATADO: { color: "status-contratado", icon: <FiCheckCircle size={14} /> },
  CANCELADO: { color: "status-cancelado", icon: <FiXCircle size={14} /> },
};

function formatDate(dateStr: string) {
  if (!dateStr) return "-";
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
}

export default function PedidoCard({ pedido, onCancelar, onAvaliar }: Props) {
  const config = statusConfig[pedido.status] || statusConfig.PENDENTE;

  return (
    <div className="card pedido-card">
      <div className="card-header">
        <div className="card-avatar pedido-avatar">
          <FiCalendar size={24} />
        </div>
        <div className="card-title">
          <h3>Pedido #{pedido.id}</h3>
          <span className={`status-badge ${config.color}`}>
            {config.icon} {pedido.statusDescricao}
          </span>
        </div>
      </div>

      <div className="card-body">
        <div className="card-field">
          <FiUser size={14} />
          <span className="value">{pedido.clienteNome}</span>
        </div>
        <div className="card-field">
          <FiTruck size={14} />
          <span className="value">{pedido.automovelDescricao}</span>
        </div>
        <div className="card-field">
          <span className="label">Período</span>
          <span className="value">
            {formatDate(pedido.dataInicio)} a {formatDate(pedido.dataFim)}
          </span>
        </div>
        {pedido.parecer && (
          <div className="card-field parecer-field">
            <span className="label">Parecer</span>
            <span className="value">{pedido.parecer}</span>
          </div>
        )}
      </div>

      <div className="card-actions">
        {onAvaliar && (pedido.status === "PENDENTE" || pedido.status === "EM_ANALISE") && (
          <button
            className="btn btn-sm btn-primary"
            onClick={() => onAvaliar(pedido.id)}
          >
            <FiCheckCircle size={14} /> Avaliar
          </button>
        )}
        {onCancelar &&
          pedido.status !== "CANCELADO" &&
          pedido.status !== "CONTRATADO" && (
            <button
              className="btn btn-sm btn-danger"
              onClick={() => onCancelar(pedido.id)}
            >
              <FiXCircle size={14} /> Cancelar
            </button>
          )}
      </div>
    </div>
  );
}
