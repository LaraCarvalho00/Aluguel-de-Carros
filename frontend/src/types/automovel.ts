export interface AutomovelRequest {
  matricula: string;
  ano: number;
  marca: string;
  modelo: string;
  placa: string;
  proprietario: string;
}

export interface AutomovelResponse {
  id: number;
  matricula: string;
  ano: number;
  marca: string;
  modelo: string;
  placa: string;
  disponivel: boolean;
  proprietario: string;
}
