import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import { clienteService } from "../services/api";
import type { ClienteRequest, ClienteResponse } from "../types/cliente";
import ClienteForm from "../components/ClienteForm";
import { extractErrorMessage } from "../utils/error";

export default function ClienteEditPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [cliente, setCliente] = useState<ClienteResponse | undefined>();
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);

  useEffect(() => {
    const fetchCliente = async () => {
      try {
        const { data } = await clienteService.buscarPorId(Number(id));
        setCliente(data);
      } catch {
        toast.error("Cliente não encontrado.");
        navigate("/clientes");
      } finally {
        setFetching(false);
      }
    };
    fetchCliente();
  }, [id, navigate]);

  const handleSubmit = async (data: ClienteRequest) => {
    try {
      setLoading(true);
      await clienteService.atualizar(Number(id), data);
      toast.success("Cliente atualizado com sucesso!");
      navigate("/clientes");
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (fetching) {
    return <div className="loading">Carregando dados do cliente...</div>;
  }

  return (
    <div className="page-form-centered">
      <h1>Editar Cliente</h1>
      <ClienteForm
        initialData={cliente}
        onSubmit={handleSubmit}
        loading={loading}
        submitLabel="Salvar Alterações"
      />
    </div>
  );
}
