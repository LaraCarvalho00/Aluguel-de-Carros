import { useState } from "react";

interface Props {
  onSubmit: (aprovado: boolean, parecer: string) => Promise<void>;
  onCancel: () => void;
}

export default function ParecerModal({ onSubmit, onCancel }: Props) {
  const [parecer, setParecer] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (aprovado: boolean) => {
    if (!parecer.trim()) return;
    setLoading(true);
    await onSubmit(aprovado, parecer);
    setLoading(false);
  };

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal parecer-modal" onClick={(e) => e.stopPropagation()}>
        <h3>Avaliar Pedido</h3>
        <p>Emita o parecer financeiro para este pedido.</p>

        <div className="form-group">
          <label htmlFor="parecer">Parecer *</label>
          <textarea
            id="parecer"
            className="form-textarea"
            rows={4}
            value={parecer}
            onChange={(e) => setParecer(e.target.value)}
            placeholder="Descreva o parecer financeiro..."
            required
          />
        </div>

        <div className="modal-actions">
          <button
            className="btn btn-secondary"
            onClick={onCancel}
            disabled={loading}
          >
            Cancelar
          </button>
          <button
            className="btn btn-danger"
            onClick={() => handleSubmit(false)}
            disabled={loading || !parecer.trim()}
          >
            Reprovar
          </button>
          <button
            className="btn btn-success"
            onClick={() => handleSubmit(true)}
            disabled={loading || !parecer.trim()}
          >
            {loading ? "Enviando..." : "Aprovar"}
          </button>
        </div>
      </div>
    </div>
  );
}
