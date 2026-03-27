import axios from "axios";
import type { ClienteRequest, ClienteResponse } from "../types/cliente";

const api = axios.create({
  baseURL: "/api/v1",
  headers: { "Content-Type": "application/json" },
});

export const clienteService = {
  listarTodos: () => api.get<ClienteResponse[]>("/clientes"),

  buscarPorId: (id: number) => api.get<ClienteResponse>(`/clientes/${id}`),

  buscarPorCpf: (cpf: string) =>
    api.get<ClienteResponse>(`/clientes/cpf/${cpf}`),

  criar: (data: ClienteRequest) =>
    api.post<ClienteResponse>("/clientes", data),

  atualizar: (id: number, data: ClienteRequest) =>
    api.put<ClienteResponse>(`/clientes/${id}`, data),

  excluir: (id: number) => api.delete(`/clientes/${id}`),
};

export default api;
