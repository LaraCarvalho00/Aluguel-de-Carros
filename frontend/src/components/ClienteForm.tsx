import { useState, useEffect, type FormEvent } from "react";
import { FiPlus, FiX } from "react-icons/fi";
import type { ClienteRequest, ClienteResponse } from "../types/cliente";

interface Props {
  initialData?: ClienteResponse;
  onSubmit: (data: ClienteRequest) => Promise<void>;
  loading: boolean;
  submitLabel: string;
}

const EMPTY_FORM: ClienteRequest = {
  rg: "",
  cpf: "",
  nome: "",
  endereco: "",
  profissao: "",
  entidadesEmpregadoras: [],
  rendimentos: 0,
};

export default function ClienteForm({ initialData, onSubmit, loading, submitLabel }: Props) {
  const [form, setForm] = useState<ClienteRequest>(EMPTY_FORM);
  const [novaEntidade, setNovaEntidade] = useState("");

  useEffect(() => {
    if (initialData) {
      setForm({
        rg: initialData.rg,
        cpf: initialData.cpf,
        nome: initialData.nome,
        endereco: initialData.endereco,
        profissao: initialData.profissao,
        entidadesEmpregadoras: initialData.entidadesEmpregadoras ?? [],
        rendimentos: initialData.rendimentos,
      });
    }
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === "rendimentos" ? Number(value) : value,
    }));
  };

  const addEntidade = () => {
    const trimmed = novaEntidade.trim();
    if (!trimmed || form.entidadesEmpregadoras.length >= 3) return;
    setForm((prev) => ({
      ...prev,
      entidadesEmpregadoras: [...prev.entidadesEmpregadoras, trimmed],
    }));
    setNovaEntidade("");
  };

  const removeEntidade = (index: number) => {
    setForm((prev) => ({
      ...prev,
      entidadesEmpregadoras: prev.entidadesEmpregadoras.filter((_, i) => i !== index),
    }));
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    onSubmit(form);
  };

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="form-grid">
        <div className="form-group">
          <label htmlFor="nome">Nome *</label>
          <input
            id="nome"
            name="nome"
            type="text"
            required
            value={form.nome}
            onChange={handleChange}
            placeholder="Nome completo"
          />
        </div>

        <div className="form-group">
          <label htmlFor="cpf">CPF *</label>
          <input
            id="cpf"
            name="cpf"
            type="text"
            required
            minLength={11}
            maxLength={14}
            value={form.cpf}
            onChange={handleChange}
            placeholder="000.000.000-00"
          />
        </div>

        <div className="form-group">
          <label htmlFor="rg">RG *</label>
          <input
            id="rg"
            name="rg"
            type="text"
            required
            value={form.rg}
            onChange={handleChange}
            placeholder="MG-00.000.000"
          />
        </div>

        <div className="form-group">
          <label htmlFor="endereco">Endereço *</label>
          <input
            id="endereco"
            name="endereco"
            type="text"
            required
            value={form.endereco}
            onChange={handleChange}
            placeholder="Rua, número, cidade"
          />
        </div>

        <div className="form-group">
          <label htmlFor="profissao">Profissão *</label>
          <input
            id="profissao"
            name="profissao"
            type="text"
            required
            value={form.profissao}
            onChange={handleChange}
            placeholder="Ex: Engenheiro"
          />
        </div>

        <div className="form-group">
          <label htmlFor="rendimentos">Rendimentos (R$) *</label>
          <input
            id="rendimentos"
            name="rendimentos"
            type="number"
            required
            min={0}
            step={0.01}
            value={form.rendimentos}
            onChange={handleChange}
            placeholder="0.00"
          />
        </div>
      </div>

      <div className="form-group">
        <label>Entidades Empregadoras (máx. 3)</label>
        <div className="entidade-input-row">
          <input
            type="text"
            value={novaEntidade}
            onChange={(e) => setNovaEntidade(e.target.value)}
            placeholder="Nome da entidade empregadora"
            disabled={form.entidadesEmpregadoras.length >= 3}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                addEntidade();
              }
            }}
          />
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={addEntidade}
            disabled={form.entidadesEmpregadoras.length >= 3 || !novaEntidade.trim()}
          >
            <FiPlus size={16} />
          </button>
        </div>
        <div className="entidade-tags">
          {form.entidadesEmpregadoras.map((ent, i) => (
            <span key={i} className="tag tag-removable">
              {ent}
              <button type="button" onClick={() => removeEntidade(i)}>
                <FiX size={12} />
              </button>
            </span>
          ))}
        </div>
      </div>

      <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
        {loading ? "Salvando..." : submitLabel}
      </button>
    </form>
  );
}
