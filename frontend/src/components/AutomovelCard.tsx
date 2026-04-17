import { useNavigate } from "react-router-dom";
import { FiEdit2, FiTrash2 } from "react-icons/fi";
import { MdDirectionsCar } from "react-icons/md";
import type { AutomovelResponse } from "../types/automovel";
import { useAuth } from "../contexts/AuthContext";

interface Props {
  automovel: AutomovelResponse;
  onDelete: (id: number) => void;
}

export default function AutomovelCard({ automovel, onDelete }: Props) {
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const isAdmin = hasRole("ADMIN");

  return (
    <div className="card">
      <div className="card-header">
        <div className="card-avatar auto-avatar">
          <MdDirectionsCar size={24} />
        </div>
        <div className="card-title">
          <h3>
            {automovel.marca} {automovel.modelo}
          </h3>
          <span className="card-id">#{automovel.id}</span>
        </div>
      </div>

      <div className="card-body">
        <div className="card-field">
          <span className="label">Placa</span>
          <span className="value">{automovel.placa}</span>
        </div>
        <div className="card-field">
          <span className="label">Matrícula</span>
          <span className="value">{automovel.matricula}</span>
        </div>
        <div className="card-field">
          <span className="label">Ano</span>
          <span className="value">{automovel.ano}</span>
        </div>
        <div className="card-field">
          <span className="label">Proprietário</span>
          <span className="value">{automovel.proprietario}</span>
        </div>
        <div className="card-field" style={{ marginTop: "0.5rem" }}>
          <span
            className={`status-badge ${automovel.disponivel ? "status-disponivel" : "status-indisponivel"}`}
          >
            {automovel.disponivel ? "Disponível" : "Indisponível"}
          </span>
        </div>
      </div>

      {isAdmin && (
        <div className="card-actions">
          <button
            className="btn btn-sm btn-primary"
            onClick={() => navigate(`/automoveis/editar/${automovel.id}`)}
          >
            <FiEdit2 size={14} /> Editar
          </button>
          <button
            className="btn btn-sm btn-danger"
            onClick={() => onDelete(automovel.id)}
          >
            <FiTrash2 size={14} /> Excluir
          </button>
        </div>
      )}
    </div>
  );
}
