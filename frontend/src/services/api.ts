import axios from "axios";
import { getApiBaseUrl } from "../config/apiBase";
import type { ClienteRequest, ClienteResponse } from "../types/cliente";
import type { AutomovelRequest, AutomovelResponse } from "../types/automovel";
import type {
  PedidoRequest,
  PedidoResponse,
  ParecerRequest,
  ContratoRequest,
  ContratoResponse,
} from "../types/pedido";

const api = axios.create({
  baseURL: getApiBaseUrl(),
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

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

export const automovelService = {
  listarTodos: () => api.get<AutomovelResponse[]>("/automoveis"),

  listarDisponiveis: () =>
    api.get<AutomovelResponse[]>("/automoveis/disponiveis"),

  buscarPorId: (id: number) =>
    api.get<AutomovelResponse>(`/automoveis/${id}`),

  criar: (data: AutomovelRequest) =>
    api.post<AutomovelResponse>("/automoveis", data),

  atualizar: (id: number, data: AutomovelRequest) =>
    api.put<AutomovelResponse>(`/automoveis/${id}`, data),

  excluir: (id: number) => api.delete(`/automoveis/${id}`),
};

export const pedidoService = {
  listarTodos: () => api.get<PedidoResponse[]>("/pedidos"),

  buscarPorId: (id: number) => api.get<PedidoResponse>(`/pedidos/${id}`),

  listarPorCliente: (clienteId: number) =>
    api.get<PedidoResponse[]>(`/pedidos/cliente/${clienteId}`),

  criar: (data: PedidoRequest) =>
    api.post<PedidoResponse>("/pedidos", data),

  atualizar: (id: number, data: PedidoRequest) =>
    api.put<PedidoResponse>(`/pedidos/${id}`, data),

  avaliar: (id: number, data: ParecerRequest) =>
    api.patch<PedidoResponse>(`/pedidos/${id}/avaliar`, data),

  cancelar: (id: number) =>
    api.patch<PedidoResponse>(`/pedidos/${id}/cancelar`),
};

export const contratoService = {
  listarTodos: () => api.get<ContratoResponse[]>("/contratos"),

  buscarPorId: (id: number) =>
    api.get<ContratoResponse>(`/contratos/${id}`),

  buscarPorPedido: (pedidoId: number) =>
    api.get<ContratoResponse>(`/contratos/pedido/${pedidoId}`),

  criar: (data: ContratoRequest) =>
    api.post<ContratoResponse>("/contratos", data),
};

export default api;
