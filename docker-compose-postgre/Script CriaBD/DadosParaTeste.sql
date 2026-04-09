INSERT INTO automoveis (matricula, ano, marca, modelo, placa, disponivel, proprietario) VALUES
('MAT123', 2020, 'Toyota', 'Corolla', 'ABC-1234', true, 'EMPRESA'),
('MAT456', 2019, 'Honda', 'Civic', 'DEF-5678', true, 'EMPRESA'),
('MAT789', 2021, 'Ford', 'Focus', 'GHI-9012', false, 'EMPRESA');

INSERT INTO clientes (rg, cpf, nome, endereco, profissao, rendimentos) VALUES
('RG123456', '123.456.789-00', 'João Silva', 'Rua A, 123', 'Engenheiro', 7500.00),
('RG654321', '987.654.321-00', 'Maria Souza', 'Rua B, 456', 'Professor', 5200.00),
('RG112233', '111.222.333-44', 'Carlos Lima', 'Rua C, 789', 'Médico', 12000.00);

INSERT INTO cliente_entidades_empregadoras (cliente_id, entidade_empregadora) VALUES
(1, 'Empresa X'),
(1, 'Empresa Y'),
(2, 'Escola ABC'),
(3, 'Hospital Central');

INSERT INTO pedidos (cliente_id, automovel_id, status, data_inicio, data_fim, parecer, data_criacao, data_atualizacao) VALUES
(1, 1, 'PENDENTE', '2026-04-01', '2026-04-10', 'Aguardando aprovação', CURRENT_TIMESTAMP, NULL),
(2, 2, 'APROVADO', '2026-04-05', '2026-04-12', 'Aprovado sem restrições', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'CANCELADO', '2026-04-03', '2026-04-08', 'Cliente cancelou', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contratos (pedido_id, valor_total, taxa_juros, parcelas, banco_agente, data_criacao) VALUES
(2, 1500.00, 2.5, 3, 'Banco do Brasil', CURRENT_TIMESTAMP),
(1, 2000.00, 3.0, 5, 'Caixa Econômica', CURRENT_TIMESTAMP);