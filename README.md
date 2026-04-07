# Sistema de Gestao de Aluguel de Carros

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Micronaut](https://img.shields.io/badge/Micronaut-4.10-1A1A1A?style=for-the-badge&logo=micronaut&logoColor=white)](https://micronaut.io/)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

Sistema web para gestao completa do ciclo de vida de alugueis de automoveis, desenvolvido para o curso de Engenharia de Software da PUC Minas.

**Equipe:** Lara Andrade, Allan Mateus, Gabriel Santiago

---

## Indice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Principios SOLID](#principios-solid)
- [Padroes de Projeto](#padroes-de-projeto)
- [Stack Tecnologica](#stack-tecnologica)
- [Modelo de Dominio](#modelo-de-dominio)
- [API Endpoints](#api-endpoints)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Diagramas UML](#diagramas-uml)
- [Historias de Usuario](#historias-de-usuario)
- [Sprints](#sprints)

---

## Sobre o Projeto

O sistema atende tres perfis principais de usuarios:

| Perfil | Responsabilidades |
|--------|-------------------|
| **Cliente** | Criar, modificar, consultar e cancelar pedidos de aluguel |
| **Agente (Empresa/Banco)** | Avaliar pedidos financeiramente, emitir pareceres e conceder contratos de credito |
| **Administrador** | Gerenciar usuarios, frota de veiculos e configuracoes do sistema |

Sobre os contratantes, armazenam-se dados de identificacao (RG, CPF, Nome, Endereco), profissao, entidades empregadoras (maximo 3) e rendimentos. Sobre os automoveis, registram-se matricula, ano, marca, modelo e placa. Dependendo do tipo de contrato, os automoveis podem ser registrados como propriedade de clientes, empresas ou bancos.

---

## Arquitetura

O projeto segue **Clean Architecture** com separacao em camadas e inversao de dependencias. O padrao **MVC** e implementado atraves de Controllers (entrada HTTP), Services (logica de negocio) e Repositories (persistencia), orquestrados por um **Facade** central.

```
                    ┌──────────────────────────┐
                    │       Controllers        │  HTTP / REST
                    └────────────┬─────────────┘
                                 │
                    ┌────────────▼─────────────┐
                    │      AluguelFacade        │  Orquestracao
                    └────────────┬─────────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
┌─────────▼──────────┐ ┌────────▼─────────┐ ┌──────────▼─────────┐
│  IClienteService   │ │  IPedidoService  │ │ IAutomovelService  │
│  IContratoService  │ │                  │ │                    │
└─────────┬──────────┘ └────────┬─────────┘ └──────────┬─────────┘
          │                      │                      │
┌─────────▼──────────┐ ┌────────▼─────────┐ ┌──────────▼─────────┐
│   ServiceImpl      │ │   ServiceImpl    │ │    ServiceImpl     │
└─────────┬──────────┘ └────────┬─────────┘ └──────────┬─────────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌────────────▼─────────────┐
                    │      Repositories        │  Micronaut Data JPA
                    └────────────┬─────────────┘
                                 │
                    ┌────────────▼─────────────┐
                    │      H2 / PostgreSQL      │
                    └──────────────────────────┘
```

### Descricao das Camadas

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| **Apresentacao** | `application.controller` | Endpoints REST, validacao de entrada, documentacao Swagger |
| **Aplicacao** | `application.service`, `application.facade` | Regras de aplicacao, orquestracao de fluxos via Facade |
| **Dominio** | `domain.entity`, `domain.enums`, `domain.exception` | Entidades JPA, enums de negocio, excecoes de dominio |
| **Infraestrutura** | `infrastructure.repository` | Persistencia de dados com Micronaut Data |
| **Transporte** | `application.dto`, `application.mapper` | DTOs imutaveis (request/response) e conversao entre camadas |
| **Tratamento de Erros** | `application.handler` | Exception handler global com respostas padronizadas |

### Diagrama de Pacotes

![Diagrama de Pacotes](docs/diagrama_pacotes.png)

---

## Principios SOLID

### S - Single Responsibility Principle

Cada classe possui uma unica responsabilidade bem definida:

```
ClienteController     → Recebe requisicoes HTTP
ClienteServiceImpl    → Executa regras de negocio
ClienteRepository     → Persiste dados
ClienteMapper         → Converte entre DTO e entidade
```

### O - Open/Closed Principle

Novos tipos de proprietario (`TipoPropriedade`) ou status de pedido (`StatusPedido`) sao adicionados via enums sem modificar o codigo existente das classes que os utilizam.

### L - Liskov Substitution Principle

Todas as implementacoes de service (`ClienteServiceImpl`, `PedidoServiceImpl`, etc.) sao substituiveis por suas interfaces (`IClienteService`, `IPedidoService`), garantindo que qualquer consumidor funcione com qualquer implementacao.

### I - Interface Segregation Principle

Interfaces de servico segregadas por dominio, cada uma com contrato coeso:

- `IClienteService` — operacoes de cliente
- `IAutomovelService` — operacoes de automovel
- `IPedidoService` — operacoes de pedido
- `IContratoService` — operacoes de contrato

### D - Dependency Inversion Principle

Controllers e Facade dependem apenas de interfaces, nunca de implementacoes concretas. A injecao de dependencia e gerenciada pelo container do Micronaut.

```java
public class PedidoController {
    private final AluguelFacade aluguelFacade;     // Depende da abstracao
    private final IPedidoService pedidoService;     // Interface, nao impl
}
```

---

## Padroes de Projeto

| Padrao | Tipo | Aplicacao no Projeto |
|--------|------|----------------------|
| **Facade** | Estrutural | `AluguelFacade` orquestra os fluxos entre pedidos, automoveis e contratos |
| **Repository** | Estrutural | Micronaut Data `CrudRepository` abstrai toda a camada de persistencia |
| **DTO** | Transferencia | Records imutaveis (`@Serdeable`) para request/response, desacoplados das entidades |
| **Mapper** | Transformacao | Classes dedicadas para conversao bidirecional entre DTOs e entidades |
| **State** | Comportamental | `StatusPedido` controla as transicoes validas do ciclo de vida do pedido |

---

## Stack Tecnologica

### Backend

| Tecnologia | Versao | Proposito |
|------------|--------|-----------|
| Java | 17+ | Linguagem principal |
| Micronaut | 4.10 | Framework HTTP (Netty) |
| Micronaut Data JPA | 4.x | Persistencia com Hibernate 6 |
| H2 | - | Banco em memoria (desenvolvimento) |
| PostgreSQL | 15+ | Banco relacional (producao) |
| OpenAPI / Swagger UI | 3.x | Documentacao interativa da API |
| JUnit 5 + Mockito | - | Testes unitarios e de integracao |
| Maven | 3.9+ | Build e gerenciamento de dependencias |

### Frontend

| Tecnologia | Versao | Proposito |
|------------|--------|-----------|
| React | 19 | Biblioteca de UI |
| TypeScript | 5.9 | Tipagem estatica |
| Vite | 8.x | Build tool e dev server |
| Axios | 1.14 | Cliente HTTP |
| React Router | 7.x | Roteamento SPA |
| React Hot Toast | 2.6 | Notificacoes |
| React Icons | 5.6 | Icones |

---

## Modelo de Dominio

### Entidades

| Entidade | Campos Principais |
|----------|-------------------|
| **Cliente** | id, rg, cpf, nome, endereco, profissao, entidadesEmpregadoras (max 3), rendimentos |
| **Automovel** | id, matricula, ano, marca, modelo, placa, disponivel, proprietario (CLIENTE/EMPRESA/BANCO) |
| **Pedido** | id, cliente, automovel, status, dataInicio, dataFim, parecer, dataCriacao, dataAtualizacao |
| **Contrato** | id, pedido, valorTotal, taxaJuros, parcelas, bancoAgente, dataCriacao |

### Enumeradores

| Enum | Valores |
|------|---------|
| `StatusPedido` | PENDENTE, EM_ANALISE, APROVADO, REPROVADO, CONTRATADO, CANCELADO |
| `TipoPropriedade` | CLIENTE, EMPRESA, BANCO |

### Ciclo de Vida do Pedido

```
  ┌──────────┐     parecer positivo     ┌───────────┐     contrato     ┌─────────────┐
  │ PENDENTE ├─────────────────────────►│ APROVADO  ├────────────────►│ CONTRATADO  │
  └────┬─────┘                          └─────┬─────┘                 └─────────────┘
       │                                      │
       │                                      │ parecer negativo
       │                                      ▼
       │                                ┌───────────┐
       │                                │ REPROVADO │
       │                                └───────────┘
       │ cancelamento
       ▼
  ┌───────────┐
  │ CANCELADO │
  └───────────┘
```

---

## API Endpoints

Documentacao interativa disponivel em `http://localhost:8080/swagger-ui/index.html`

### Clientes `/api/v1/clientes`

| Metodo | Rota | Descricao |
|--------|------|-----------|
| `POST` | `/` | Cadastrar cliente |
| `GET` | `/` | Listar todos |
| `GET` | `/{id}` | Buscar por ID |
| `GET` | `/cpf/{cpf}` | Buscar por CPF |
| `PUT` | `/{id}` | Atualizar |
| `DELETE` | `/{id}` | Remover |

### Automoveis `/api/v1/automoveis`

| Metodo | Rota | Descricao |
|--------|------|-----------|
| `POST` | `/` | Cadastrar automovel |
| `GET` | `/` | Listar todos |
| `GET` | `/disponiveis` | Listar disponiveis para aluguel |
| `GET` | `/{id}` | Buscar por ID |
| `PUT` | `/{id}` | Atualizar |
| `DELETE` | `/{id}` | Remover |

### Pedidos `/api/v1/pedidos`

| Metodo | Rota | Descricao |
|--------|------|-----------|
| `POST` | `/` | Criar pedido de aluguel |
| `GET` | `/` | Listar todos |
| `GET` | `/{id}` | Buscar por ID |
| `GET` | `/cliente/{clienteId}` | Listar pedidos de um cliente |
| `PUT` | `/{id}` | Modificar pedido pendente |
| `PATCH` | `/{id}/avaliar` | Emitir parecer financeiro (aprovar/reprovar) |
| `PATCH` | `/{id}/cancelar` | Cancelar pedido |

### Contratos `/api/v1/contratos`

| Metodo | Rota | Descricao |
|--------|------|-----------|
| `POST` | `/` | Executar contrato de credito |
| `GET` | `/` | Listar todos |
| `GET` | `/{id}` | Buscar por ID |
| `GET` | `/pedido/{pedidoId}` | Buscar contrato por pedido |

---

## Estrutura do Projeto

### Backend

```
src/main/java/com/pucminas/aluguelcarros/
├── Application.java
├── application/
│   ├── controller/
│   │   ├── ClienteController.java
│   │   ├── AutomovelController.java
│   │   ├── PedidoController.java
│   │   └── ContratoController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ClienteRequestDTO.java
│   │   │   ├── AutomovelRequestDTO.java
│   │   │   ├── PedidoRequestDTO.java
│   │   │   ├── ParecerRequestDTO.java
│   │   │   └── ContratoRequestDTO.java
│   │   └── response/
│   │       ├── ClienteResponseDTO.java
│   │       ├── AutomovelResponseDTO.java
│   │       ├── PedidoResponseDTO.java
│   │       └── ContratoResponseDTO.java
│   ├── facade/
│   │   └── AluguelFacade.java
│   ├── handler/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   ├── mapper/
│   │   ├── ClienteMapper.java
│   │   ├── AutomovelMapper.java
│   │   ├── PedidoMapper.java
│   │   └── ContratoMapper.java
│   └── service/
│       ├── IClienteService.java
│       ├── IAutomovelService.java
│       ├── IPedidoService.java
│       ├── IContratoService.java
│       └── impl/
│           ├── ClienteServiceImpl.java
│           ├── AutomovelServiceImpl.java
│           ├── PedidoServiceImpl.java
│           └── ContratoServiceImpl.java
├── domain/
│   ├── entity/
│   │   ├── Cliente.java
│   │   ├── Automovel.java
│   │   ├── Pedido.java
│   │   └── Contrato.java
│   ├── enums/
│   │   ├── StatusPedido.java
│   │   └── TipoPropriedade.java
│   └── exception/
│       ├── BusinessException.java
│       └── ResourceNotFoundException.java
└── infrastructure/
    └── repository/
        ├── ClienteRepository.java
        ├── AutomovelRepository.java
        ├── PedidoRepository.java
        └── ContratoRepository.java
```

### Frontend

```
frontend/src/
├── components/
│   ├── Header.tsx
│   ├── ClienteCard.tsx
│   ├── ClienteForm.tsx
│   ├── AutomovelCard.tsx
│   ├── AutomovelForm.tsx
│   ├── PedidoCard.tsx
│   ├── ParecerModal.tsx
│   └── ConfirmModal.tsx
├── pages/
│   ├── Home.tsx
│   ├── ClienteListPage.tsx
│   ├── ClienteCreatePage.tsx
│   ├── ClienteEditPage.tsx
│   ├── ClienteSearchPage.tsx
│   ├── AutomovelListPage.tsx
│   ├── AutomovelCreatePage.tsx
│   ├── AutomovelEditPage.tsx
│   ├── PedidoListPage.tsx
│   └── PedidoCreatePage.tsx
├── services/
│   └── api.ts
├── types/
│   ├── cliente.ts
│   ├── automovel.ts
│   └── pedido.ts
├── utils/
│   └── error.ts
├── App.tsx
├── main.tsx
└── index.css
```

---

## Como Executar

### Pre-requisitos

- JDK 17+
- Node.js 18+
- Maven 3.9+ (wrapper incluso no projeto)

### Backend

```bash
./mvnw mn:run
```

Inicia na porta `8080` com banco H2 em memoria. Swagger UI disponivel em `/swagger-ui/index.html`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Inicia na porta `3000` com proxy configurado para o backend.

### Testes

```bash
./mvnw test
```

---

## Diagramas UML

Disponiveis no diretorio `/docs`:

| Diagrama | Descricao |
|----------|-----------|
| Casos de Uso | Interacoes entre atores e funcionalidades |
| Classes | Estrutura de dados e relacionamentos do dominio |
| Pacotes (Visao Logica) | Organizacao em camadas da arquitetura |
| Componentes | Modulos do sistema e suas dependencias |

---

## Historias de Usuario

Documentadas em [`HISTORIAS_USUARIO.md`](HISTORIAS_USUARIO.md), cobrindo:

| Grupo | Historias | Descricao |
|-------|-----------|-----------|
| Cadastro e Autenticacao | US01 - US03 | Registro de clientes e acesso ao sistema |
| Fluxo de Aluguel | US04 - US06 | Solicitacao, gestao e acompanhamento de pedidos |
| Gestao e Aprovacao | US07 - US09 | Avaliacao financeira e execucao de contratos |
| Financiamento | US10 - US11 | Concessao e associacao de contratos de credito |
| Gestao de Frota | US12 | Manutencao dos dados de automoveis |

---

## Sprints

| Sprint | Entregaveis |
|--------|-------------|
| **Lab02S01** | Diagrama de Casos de Uso, Historias de Usuario, Diagrama de Classes, Diagrama de Pacotes |
| **Lab02S02** | Revisao dos diagramas + Diagrama de Componentes + CRUD de Cliente (web, Java, MVC) |
| **Lab02S03** | Revisao dos diagramas + Diagrama de Implantacao + Prototipo completo com pedidos de aluguel |

---

<p align="center">
  <strong>PUC Minas</strong> — Laboratorio de Desenvolvimento de Software
</p>
