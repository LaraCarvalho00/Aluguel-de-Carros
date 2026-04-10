import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { FiPlus, FiInbox, FiFileText, FiDollarSign } from "react-icons/fi";
import toast from "react-hot-toast";
import { contratoService } from "../services/api";
import type { ContratoResponse } from "../types/pedido";
import { useAuth } from "../contexts/AuthContext";

export default function ContratoListPage() {
  const { hasRole } = useAuth();
  const podeExecutar = hasRole("AGENTE") || hasRole("ADMIN");
  const [contratos, setContratos] = useState<ContratoResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    contratoService.listarTodos()
      .then(({ data }) => setContratos(data))
      .catch(() => toast.error("Erro ao carregar contratos."))
      .finally(() => setLoading(false));
  }, []);

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value);

  if (loading) return <div className="loading">Carregando contratos...</div>;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Contratos de Crédito</h1>
        {podeExecutar && (
          <Link to="/contratos/novo" className="btn btn-primary">
            <FiPlus size={16} /> Executar Contrato
          </Link>
        )}
      </div>

      {contratos.length === 0 ? (
        <div className="empty-state">
          <FiInbox size={48} />
          <h2>Nenhum contrato registrado</h2>
          {podeExecutar && (
            <>
              <p>Execute um contrato para um pedido aprovado.</p>
              <Link to="/contratos/novo" className="btn btn-primary">
                Executar Contrato
              </Link>
            </>
          )}
        </div>
      ) : (
        <div className="cards-grid">
          {contratos.map((contrato) => (
            <div key={contrato.id} className="card">
              <div className="card-header">
                <div className="card-avatar pedido-avatar">
                  <FiFileText size={24} />
                </div>
                <div className="card-title">
                  <h3>Contrato #{contrato.id}</h3>
                  <span className="card-id">Pedido #{contrato.pedidoId}</span>
                </div>
              </div>

              <div className="card-body">
                <div className="card-field">
                  <span className="label">Cliente</span>
                  <span className="value">{contrato.clienteNome}</span>
                </div>
                <div className="card-field">
                  <span className="label">Veículo</span>
                  <span className="value">{contrato.automovelDescricao}</span>
                </div>
                <div className="card-field">
                  <FiDollarSign size={14} />
                  <span className="value currency">{formatCurrency(contrato.valorTotal)}</span>
                </div>
                <div className="card-field">
                  <span className="label">Parcelas</span>
                  <span className="value">{contrato.parcelas}x | Juros: {contrato.taxaJuros}%</span>
                </div>
                <div className="card-field">
                  <span className="label">Banco</span>
                  <span className="value">{contrato.bancoAgente || "—"}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
