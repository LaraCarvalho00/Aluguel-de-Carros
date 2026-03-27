import { useState, type FormEvent } from "react";
import { FiSearch } from "react-icons/fi";
import toast from "react-hot-toast";
import { clienteService } from "../services/api";
import type { ClienteResponse } from "../types/cliente";
import ClienteCard from "../components/ClienteCard";
import ConfirmModal from "../components/ConfirmModal";

export default function ClienteSearchPage() {
  const [cpf, setCpf] = useState("");
  const [cliente, setCliente] = useState<ClienteResponse | null>(null);
  const [searched, setSearched] = useState(false);
  const [loading, setLoading] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [deleting, setDeleting] = useState(false);

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    if (!cpf.trim()) return;
    try {
      setLoading(true);
      setSearched(true);
      const { data } = await clienteService.buscarPorCpf(cpf.trim());
      setCliente(data);
    } catch {
      setCliente(null);
      toast.error("Cliente não encontrado para o CPF informado.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (deleteId === null) return;
    try {
      setDeleting(true);
      await clienteService.excluir(deleteId);
      toast.success("Cliente excluído com sucesso!");
      setCliente(null);
      setSearched(false);
      setCpf("");
    } catch {
      toast.error("Erro ao excluir cliente.");
    } finally {
      setDeleting(false);
      setDeleteId(null);
    }
  };

  return (
    <div className="page">
      <h1>Buscar Cliente por CPF</h1>

      <form className="search-form" onSubmit={handleSearch}>
        <input
          type="text"
          value={cpf}
          onChange={(e) => setCpf(e.target.value)}
          placeholder="Digite o CPF do cliente..."
          minLength={11}
          maxLength={14}
        />
        <button type="submit" className="btn btn-primary" disabled={loading}>
          <FiSearch size={16} /> {loading ? "Buscando..." : "Buscar"}
        </button>
      </form>

      {searched && !loading && cliente && (
        <div className="search-result">
          <ClienteCard cliente={cliente} onDelete={setDeleteId} />
        </div>
      )}

      {searched && !loading && !cliente && (
        <div className="empty-state">
          <FiSearch size={48} />
          <h2>Nenhum resultado</h2>
          <p>Não foi encontrado nenhum cliente com o CPF informado.</p>
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
