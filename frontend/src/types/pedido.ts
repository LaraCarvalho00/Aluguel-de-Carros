export interface PedidoRequest {
  clienteId: number;
  automovelId: number;
  dataInicio: string;
  dataFim: string;
}

export interface PedidoResponse {
  id: number;
  clienteId: number;
  clienteNome: string;
  automovelId: number;
  automovelDescricao: string;
  status: string;
  statusDescricao: string;
  dataInicio: string;
  dataFim: string;
  parecer: string | null;
  dataCriacao: string;
  dataAtualizacao: string | null;
}

export interface ParecerRequest {
  aprovado: boolean;
  parecer: string;
}

export interface ContratoRequest {
  pedidoId: number;
  valorTotal: number;
  taxaJuros: number;
  parcelas: number;
  bancoAgente: string;
}

export interface ContratoResponse {
  id: number;
  pedidoId: number;
  clienteNome: string;
  automovelDescricao: string;
  valorTotal: number;
  taxaJuros: number;
  parcelas: number;
  bancoAgente: string;
  dataCriacao: string;
}
