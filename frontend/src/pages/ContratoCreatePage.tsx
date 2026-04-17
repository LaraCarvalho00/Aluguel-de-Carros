import { useState, useEffect, FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { FiFileText } from "react-icons/fi";
import toast from "react-hot-toast";
import { pedidoService, contratoService } from "../services/api";
import type { PedidoResponse } from "../types/pedido";

export default function ContratoCreatePage() {
  const navigate = useNavigate();
  const [pedidosAprovados, setPedidosAprovados] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [pedidoId, setPedidoId] = useState<number | "">("");
  const [valorTotal, setValorTotal] = useState("");
  const [taxaJuros, setTaxaJuros] = useState("");
  const [parcelas, setParcelas] = useState("");
  const [bancoAgente, setBancoAgente] = useState("");

  useEffect(() => {
    pedidoService.listarTodos().then(({ data }) => {
      setPedidosAprovados(data.filter((p) => p.status === "APROVADO"));
    }).catch(() => {
      toast.error("Erro ao carregar pedidos aprovados.");
    }).finally(() => setLoading(false));
  }, []);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!pedidoId) return;
    setSaving(true);
    try {
      await contratoService.criar({
        pedidoId: Number(pedidoId),
        valorTotal: parseFloat(valorTotal),
        taxaJuros: parseFloat(taxaJuros),
        parcelas: parseInt(parcelas),
        bancoAgente,
      });
      toast.success("Contrato executado com sucesso!");
      navigate("/contratos");
    } catch {
      toast.error("Erro ao executar contrato. Verifique se já não existe um contrato para este pedido.");
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <div className="loading">Carregando pedidos aprovados...</div>;

  return (
    <div className="page-form-centered">
      <div className="page-header">
        <h1>Executar Contrato</h1>
      </div>

      {pedidosAprovados.length === 0 ? (
        <div className="empty-state">
          <FiFileText size={48} />
          <h2>Nenhum pedido aprovado disponível</h2>
          <p>Só é possível executar contratos para pedidos com status <strong>Aprovado</strong>.</p>
        </div>
      ) : (
        <div className="form-card">
          <form onSubmit={handleSubmit} className="form">
            <div className="form-group">
              <label htmlFor="pedidoId">Pedido Aprovado *</label>
              <select
                id="pedidoId"
                value={pedidoId}
                onChange={(e) => setPedidoId(Number(e.target.value))}
                required
              >
                <option value="">Selecione um pedido...</option>
                {pedidosAprovados.map((p) => (
                  <option key={p.id} value={p.id}>
                    #{p.id} — {p.clienteNome} | {p.automovelDescricao}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="valorTotal">Valor Total (R$) *</label>
                <input
                  id="valorTotal"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="Ex: 1500.00"
                  value={valorTotal}
                  onChange={(e) => setValorTotal(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="taxaJuros">Taxa de Juros (%) *</label>
                <input
                  id="taxaJuros"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="Ex: 2.50"
                  value={taxaJuros}
                  onChange={(e) => setTaxaJuros(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="parcelas">Parcelas *</label>
                <input
                  id="parcelas"
                  type="number"
                  min="1"
                  placeholder="Ex: 12"
                  value={parcelas}
                  onChange={(e) => setParcelas(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="bancoAgente">Banco Agente *</label>
                <input
                  id="bancoAgente"
                  type="text"
                  placeholder="Ex: Banco do Brasil"
                  value={bancoAgente}
                  onChange={(e) => setBancoAgente(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => navigate("/contratos")}>
                Cancelar
              </button>
              <button type="submit" className="btn btn-primary" disabled={saving}>
                <FiFileText size={16} />
                {saving ? "Executando..." : "Executar Contrato"}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
