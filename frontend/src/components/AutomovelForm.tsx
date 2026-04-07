import { useState, useEffect, type FormEvent } from "react";
import type { AutomovelRequest, AutomovelResponse } from "../types/automovel";

interface Props {
  initialData?: AutomovelResponse;
  onSubmit: (data: AutomovelRequest) => Promise<void>;
  loading: boolean;
  submitLabel: string;
}

const EMPTY_FORM: AutomovelRequest = {
  matricula: "",
  ano: new Date().getFullYear(),
  marca: "",
  modelo: "",
  placa: "",
  proprietario: "EMPRESA",
};

export default function AutomovelForm({
  initialData,
  onSubmit,
  loading,
  submitLabel,
}: Props) {
  const [form, setForm] = useState<AutomovelRequest>(EMPTY_FORM);

  useEffect(() => {
    if (initialData) {
      setForm({
        matricula: initialData.matricula,
        ano: initialData.ano,
        marca: initialData.marca,
        modelo: initialData.modelo,
        placa: initialData.placa,
        proprietario: initialData.proprietario,
      });
    }
  }, [initialData]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === "ano" ? Number(value) : value,
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
          <label htmlFor="marca">Marca *</label>
          <input
            id="marca"
            name="marca"
            type="text"
            required
            value={form.marca}
            onChange={handleChange}
            placeholder="Ex: Toyota"
          />
        </div>

        <div className="form-group">
          <label htmlFor="modelo">Modelo *</label>
          <input
            id="modelo"
            name="modelo"
            type="text"
            required
            value={form.modelo}
            onChange={handleChange}
            placeholder="Ex: Corolla"
          />
        </div>

        <div className="form-group">
          <label htmlFor="ano">Ano *</label>
          <input
            id="ano"
            name="ano"
            type="number"
            required
            min={1900}
            max={2030}
            value={form.ano}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label htmlFor="placa">Placa *</label>
          <input
            id="placa"
            name="placa"
            type="text"
            required
            value={form.placa}
            onChange={handleChange}
            placeholder="ABC-1234"
          />
        </div>

        <div className="form-group">
          <label htmlFor="matricula">Matrícula *</label>
          <input
            id="matricula"
            name="matricula"
            type="text"
            required
            value={form.matricula}
            onChange={handleChange}
            placeholder="Número da matrícula"
          />
        </div>

        <div className="form-group">
          <label htmlFor="proprietario">Proprietário</label>
          <select
            id="proprietario"
            name="proprietario"
            value={form.proprietario}
            onChange={handleChange}
            className="form-select"
          >
            <option value="EMPRESA">Empresa</option>
            <option value="CLIENTE">Cliente</option>
            <option value="BANCO">Banco</option>
          </select>
        </div>
      </div>

      <button
        type="submit"
        className="btn btn-primary btn-block"
        disabled={loading}
      >
        {loading ? "Salvando..." : submitLabel}
      </button>
    </form>
  );
}
