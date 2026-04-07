import { useState, useEffect, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import {
  clienteService,
  automovelService,
  pedidoService,
} from "../services/api";
import type { ClienteResponse } from "../types/cliente";
import type { AutomovelResponse } from "../types/automovel";
import type { PedidoRequest } from "../types/pedido";
import { extractErrorMessage } from "../utils/error";

export default function PedidoCreatePage() {
  const navigate = useNavigate();
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [automoveis, setAutomoveis] = useState<AutomovelResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);

  const [form, setForm] = useState<PedidoRequest>({
    clienteId: 0,
    automovelId: 0,
    dataInicio: "",
    dataFim: "",
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [clientesRes, automoveisRes] = await Promise.all([
          clienteService.listarTodos(),
          automovelService.listarDisponiveis(),
        ]);
        setClientes(clientesRes.data);
        setAutomoveis(automoveisRes.data);
      } catch {
        toast.error("Erro ao carregar dados.");
      } finally {
        setFetching(false);
      }
    };
    fetchData();
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]:
        name === "clienteId" || name === "automovelId" ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!form.clienteId || !form.automovelId) {
      toast.error("Selecione o cliente e o automóvel.");
      return;
    }
    try {
      setLoading(true);
      await pedidoService.criar(form);
      toast.success("Pedido criado com sucesso!");
      navigate("/pedidos");
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (fetching)
    return <div className="loading">Carregando dados...</div>;

  return (
    <div className="page">
      <h1>Novo Pedido de Aluguel</h1>
      <form className="form" onSubmit={handleSubmit}>
        <div className="form-grid">
          <div className="form-group">
            <label htmlFor="clienteId">Cliente *</label>
            <select
              id="clienteId"
              name="clienteId"
              required
              value={form.clienteId}
              onChange={handleChange}
              className="form-select"
            >
              <option value={0} disabled>
                Selecione um cliente
              </option>
              {clientes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nome} - CPF: {c.cpf}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="automovelId">Automóvel *</label>
            <select
              id="automovelId"
              name="automovelId"
              required
              value={form.automovelId}
              onChange={handleChange}
              className="form-select"
            >
              <option value={0} disabled>
                Selecione um automóvel
              </option>
              {automoveis.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.marca} {a.modelo} {a.ano} - {a.placa}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="dataInicio">Data de Início *</label>
            <input
              id="dataInicio"
              name="dataInicio"
              type="date"
              required
              value={form.dataInicio}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="dataFim">Data de Fim *</label>
            <input
              id="dataFim"
              name="dataFim"
              type="date"
              required
              value={form.dataFim}
              onChange={handleChange}
            />
          </div>
        </div>

        <button
          type="submit"
          className="btn btn-primary btn-block"
          disabled={loading}
        >
          {loading ? "Criando..." : "Criar Pedido"}
        </button>
      </form>
    </div>
  );
}
