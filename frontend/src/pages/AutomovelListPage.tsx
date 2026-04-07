import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { FiPlus, FiInbox } from "react-icons/fi";
import toast from "react-hot-toast";
import { automovelService } from "../services/api";
import type { AutomovelResponse } from "../types/automovel";
import AutomovelCard from "../components/AutomovelCard";
import ConfirmModal from "../components/ConfirmModal";

export default function AutomovelListPage() {
  const [automoveis, setAutomoveis] = useState<AutomovelResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [deleting, setDeleting] = useState(false);

  const fetchAutomoveis = async () => {
    try {
      setLoading(true);
      const { data } = await automovelService.listarTodos();
      setAutomoveis(data);
    } catch {
      toast.error("Erro ao carregar automóveis.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAutomoveis();
  }, []);

  const handleDelete = async () => {
    if (deleteId === null) return;
    try {
      setDeleting(true);
      await automovelService.excluir(deleteId);
      toast.success("Automóvel excluído com sucesso!");
      setAutomoveis((prev) => prev.filter((a) => a.id !== deleteId));
    } catch {
      toast.error("Erro ao excluir automóvel.");
    } finally {
      setDeleting(false);
      setDeleteId(null);
    }
  };

  if (loading) {
    return <div className="loading">Carregando automóveis...</div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Frota de Automóveis</h1>
        <Link to="/automoveis/novo" className="btn btn-primary">
          <FiPlus size={16} /> Novo Automóvel
        </Link>
      </div>

      {automoveis.length === 0 ? (
        <div className="empty-state">
          <FiInbox size={48} />
          <h2>Nenhum automóvel cadastrado</h2>
          <p>Comece cadastrando o primeiro veículo da frota.</p>
          <Link to="/automoveis/novo" className="btn btn-primary">
            Cadastrar Automóvel
          </Link>
        </div>
      ) : (
        <div className="cards-grid">
          {automoveis.map((automovel) => (
            <AutomovelCard
              key={automovel.id}
              automovel={automovel}
              onDelete={setDeleteId}
            />
          ))}
        </div>
      )}

      {deleteId !== null && (
        <ConfirmModal
          title="Excluir Automóvel"
          message="Tem certeza que deseja excluir este automóvel? Esta ação não pode ser desfeita."
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
          loading={deleting}
        />
      )}
    </div>
  );
}
