CREATE TABLE automoveis (
    id BIGSERIAL PRIMARY KEY,
    matricula VARCHAR(255) NOT NULL UNIQUE,
    ano INT NOT NULL,
    marca VARCHAR(255) NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    placa VARCHAR(255) NOT NULL UNIQUE,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    proprietario VARCHAR(50) NOT NULL
);

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    rg VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    profissao VARCHAR(255) NOT NULL,
    rendimentos NUMERIC(15,2) NOT NULL
);

CREATE TABLE cliente_entidades_empregadoras (
    cliente_id BIGINT NOT NULL,
    entidade_empregadora VARCHAR(255) NOT NULL,
    PRIMARY KEY (cliente_id, entidade_empregadora),
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    automovel_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    parecer VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_cliente_pedido FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_automovel_pedido FOREIGN KEY (automovel_id) REFERENCES automoveis(id)
);

CREATE TABLE contratos (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    valor_total NUMERIC(15,2) NOT NULL,
    taxa_juros NUMERIC(5,2) NOT NULL,
    parcelas INT NOT NULL,
    banco_agente VARCHAR(100),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_contrato FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);

CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_automovel ON pedidos(automovel_id);
CREATE INDEX idx_cliente_entidades_empregadoras_cliente ON cliente_entidades_empregadoras(cliente_id);