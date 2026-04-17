import { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { automovelService } from "../services/api";
import type { AutomovelRequest } from "../types/automovel";
import AutomovelForm from "../components/AutomovelForm";
import { extractErrorMessage } from "../utils/error";

export default function AutomovelCreatePage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (data: AutomovelRequest) => {
    try {
      setLoading(true);
      await automovelService.criar(data);
      toast.success("Automóvel cadastrado com sucesso!");
      navigate("/automoveis");
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-form-centered">
      <h1>Cadastrar Novo Automóvel</h1>
      <AutomovelForm
        onSubmit={handleSubmit}
        loading={loading}
        submitLabel="Cadastrar"
      />
    </div>
  );
}
