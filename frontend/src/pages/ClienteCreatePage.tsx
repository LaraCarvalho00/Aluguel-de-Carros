import { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { clienteService } from "../services/api";
import type { ClienteRequest } from "../types/cliente";
import ClienteForm from "../components/ClienteForm";
import { extractErrorMessage } from "../utils/error";

export default function ClienteCreatePage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (data: ClienteRequest) => {
    try {
      setLoading(true);
      await clienteService.criar(data);
      toast.success("Cliente cadastrado com sucesso!");
      navigate("/clientes");
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <h1>Cadastrar Novo Cliente</h1>
      <ClienteForm onSubmit={handleSubmit} loading={loading} submitLabel="Cadastrar" />
    </div>
  );
}
