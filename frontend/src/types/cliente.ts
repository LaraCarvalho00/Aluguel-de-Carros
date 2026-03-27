export interface ClienteRequest {
  rg: string;
  cpf: string;
  nome: string;
  endereco: string;
  profissao: string;
  entidadesEmpregadoras: string[];
  rendimentos: number;
}

export interface ClienteResponse {
  id: number;
  rg: string;
  cpf: string;
  nome: string;
  endereco: string;
  profissao: string;
  entidadesEmpregadoras: string[];
  rendimentos: number;
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}
