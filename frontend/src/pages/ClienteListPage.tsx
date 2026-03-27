import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { FiUserPlus, FiInbox } from "react-icons/fi";
import toast from "react-hot-toast";
import { clienteService } from "../services/api";
import type { ClienteResponse } from "../types/cliente";
import ClienteCard from "../components/ClienteCard";
import ConfirmModal from "../components/ConfirmModal";

export default function ClienteListPage() {
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [deleting, setDeleting] = useState(false);

  const fetchClientes = async () => {
    try {
      setLoading(true);
      const { data } = await clienteService.listarTodos();
      setClientes(data);
    } catch {
      toast.error("Erro ao carregar clientes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClientes();
  }, []);

  const handleDelete = async () => {
    if (deleteId === null) return;
    try {
      setDeleting(true);
      await clienteService.excluir(deleteId);
      toast.success("Cliente excluído com sucesso!");
      setClientes((prev) => prev.filter((c) => c.id !== deleteId));
    } catch {
      toast.error("Erro ao excluir cliente.");
    } finally {
      setDeleting(false);
      setDeleteId(null);
    }
  };

  if (loading) {
    return <div className="loading">Carregando clientes...</div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Clientes Cadastrados</h1>
        <Link to="/clientes/novo" className="btn btn-primary">
          <FiUserPlus size={16} /> Novo Cliente
        </Link>
      </div>

      {clientes.length === 0 ? (
        <div className="empty-state">
          <FiInbox size={48} />
          <h2>Nenhum cliente cadastrado</h2>
          <p>Comece cadastrando seu primeiro cliente.</p>
          <Link to="/clientes/novo" className="btn btn-primary">
            Cadastrar Cliente
          </Link>
        </div>
      ) : (
        <div className="cards-grid">
          {clientes.map((cliente) => (
            <ClienteCard key={cliente.id} cliente={cliente} onDelete={setDeleteId} />
          ))}
        </div>
      )}

      {deleteId !== null && (
        <ConfirmModal
          title="Excluir Cliente"
          message="Tem certeza que deseja excluir este cliente? Esta ação não pode ser desfeita."
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
          loading={deleting}
        />
      )}
    </div>
  );
}
