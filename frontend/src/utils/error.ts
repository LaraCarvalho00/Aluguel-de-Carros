import axios from "axios";
import type { ErrorResponse } from "../types/cliente";

export function extractErrorMessage(err: unknown): string {
  if (axios.isAxiosError(err) && err.response?.data) {
    const data = err.response.data as ErrorResponse;
    return data.message || "Erro inesperado do servidor.";
  }
  return "Erro de conexão com o servidor.";
}
