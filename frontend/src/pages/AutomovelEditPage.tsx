import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import { automovelService } from "../services/api";
import type { AutomovelRequest, AutomovelResponse } from "../types/automovel";
import AutomovelForm from "../components/AutomovelForm";
import { extractErrorMessage } from "../utils/error";

export default function AutomovelEditPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [automovel, setAutomovel] = useState<AutomovelResponse>();
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);

  useEffect(() => {
    const fetch = async () => {
      try {
        const { data } = await automovelService.buscarPorId(Number(id));
        setAutomovel(data);
      } catch {
        toast.error("Automóvel não encontrado.");
        navigate("/automoveis");
      } finally {
        setFetching(false);
      }
    };
    fetch();
  }, [id, navigate]);

  const handleSubmit = async (data: AutomovelRequest) => {
    try {
      setLoading(true);
      await automovelService.atualizar(Number(id), data);
      toast.success("Automóvel atualizado com sucesso!");
      navigate("/automoveis");
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (fetching) return <div className="loading">Carregando...</div>;

  return (
    <div className="page">
      <h1>Editar Automóvel</h1>
      <AutomovelForm
        initialData={automovel}
        onSubmit={handleSubmit}
        loading={loading}
        submitLabel="Salvar Alterações"
      />
    </div>
  );
}
