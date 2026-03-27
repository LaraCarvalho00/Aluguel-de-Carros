import { useNavigate } from "react-router-dom";
import { FiEdit2, FiTrash2, FiUser, FiBriefcase, FiMapPin } from "react-icons/fi";
import type { ClienteResponse } from "../types/cliente";

interface Props {
  cliente: ClienteResponse;
  onDelete: (id: number) => void;
}

export default function ClienteCard({ cliente, onDelete }: Props) {
  const navigate = useNavigate();
  const entidades = cliente.entidadesEmpregadoras ?? [];

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value);

  return (
    <div className="card">
      <div className="card-header">
        <div className="card-avatar">
          <FiUser size={24} />
        </div>
        <div className="card-title">
          <h3>{cliente.nome}</h3>
          <span className="card-id">#{cliente.id}</span>
        </div>
      </div>

      <div className="card-body">
        <div className="card-field">
          <span className="label">CPF</span>
          <span className="value">{cliente.cpf}</span>
        </div>
        <div className="card-field">
          <span className="label">RG</span>
          <span className="value">{cliente.rg}</span>
        </div>
        <div className="card-field">
          <FiMapPin size={14} />
          <span className="value">{cliente.endereco}</span>
        </div>
        <div className="card-field">
          <FiBriefcase size={14} />
          <span className="value">{cliente.profissao}</span>
        </div>
        {entidades.length > 0 && (
          <div className="card-tags">
            {entidades.map((e, i) => (
              <span key={i} className="tag">{e}</span>
            ))}
          </div>
        )}
        <div className="card-field rendimentos">
          <span className="label">Rendimentos</span>
          <span className="value currency">{formatCurrency(cliente.rendimentos)}</span>
        </div>
      </div>

      <div className="card-actions">
        <button
          className="btn btn-sm btn-primary"
          onClick={() => navigate(`/clientes/editar/${cliente.id}`)}
        >
          <FiEdit2 size={14} /> Editar
        </button>
        <button
          className="btn btn-sm btn-danger"
          onClick={() => onDelete(cliente.id)}
        >
          <FiTrash2 size={14} /> Excluir
        </button>
      </div>
    </div>
  );
}
