# Sistema de Gestao de Aluguel de Carros

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Micronaut](https://img.shields.io/badge/Micronaut-4.10-1A1A1A?style=for-the-badge&logo=micronaut&logoColor=white)](https://micronaut.io/)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

Sistema web para gestao completa do ciclo de vida de alugueis de automoveis, desenvolvido para o curso de Engenharia de Software da PUC Minas.

**Equipe:** Lara Andrade, Allan Mateus, Gabriel Santiago

---

## Indice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Seguranca e Papeis](#seguranca-e-papeis)
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
| **Cliente** | Criar, modificar, consultar e cancelar apenas os seus proprios pedidos de aluguel |
| **Agente (Empresa/Banco)** | Avaliar pedidos financeiramente, emitir pareceres e formalizar contratos de credito |
| **Administrador** | Gerenciar clientes, frota de veiculos, pedidos, contratos e todos os dados do sistema |

Sobre os contratantes, armazenam-se dados de identificacao (RG, CPF, Nome, Endereco), profissao, entidades empregadoras (maximo 3) e rendimentos. Sobre os automoveis, registram-se matricula, ano, marca, modelo e placa. Dependendo do tipo de contrato, os automoveis podem ser registrados como propriedade de clientes, empresas ou bancos.

---

## Arquitetura

O projeto segue **Clean Architecture** com separacao em camadas e inversao de dependencias. O padrao **MVC** e implementado atraves de Controllers (entrada HTTP), Services (logica de negocio) e Repositories (persistencia), orquestrados por um **Facade** central.

```
                    ┌──────────────────────────┐
                    │    Micronaut Security     │  JWT / Bearer Token
                    └────────────┬─────────────┘
                                 │
                    ┌────────────▼─────────────┐
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
                    │        PostgreSQL         │
                    └──────────────────────────┘
```

### Descricao das Camadas

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| **Apresentacao** | `application.controller` | Endpoints REST, validacao de entrada, documentacao Swagger |
| **Aplicacao** | `application.service`, `application.facade` | Regras de aplicacao, orquestracao de fluxos via Facade |
| **Dominio** | `domain.entity`, `domain.enums`, `domain.exception` | Entidades JPA, enums de negocio, excecoes de dominio |
| **Infraestrutura** | `infrastructure.repository`, `infrastructure.security` | Persistencia de dados e autenticacao JWT |
| **Transporte** | `application.dto`, `application.mapper` | DTOs imutaveis (request/response) e conversao entre camadas |
| **Tratamento de Erros** | `application.handler` | Exception handler global com respostas padronizadas |

---

## Seguranca e Papeis

O sistema utiliza **JWT (JSON Web Token)** via Micronaut Security. Todos os endpoints (exceto `/api/v1/auth/registro` e `/api/v1/auth/login`) exigem autenticacao.

### Matriz de Permissoes por Endpoint

| Endpoint | CLIENTE | AGENTE | ADMIN |
|----------|:-------:|:------:|:-----:|
| `POST /clientes` | sim | sim | sim |
| `GET /clientes` (todos) | nao | sim | sim |
| `GET /clientes/{id}` | sim | sim | sim |
| `GET /clientes/cpf/{cpf}` | sim | sim | sim |
| `PUT /clientes/{id}` | sim | sim | sim |
| `DELETE /clientes/{id}` | nao | nao | sim |
| `GET /automoveis` (todos) | nao | sim | sim |
| `GET /automoveis/disponiveis` | sim | sim | sim |
| `POST /automoveis` | nao | nao | sim |
| `PUT/DELETE /automoveis/{id}` | nao | nao | sim |
| `POST /pedidos` | sim (proprio) | nao | sim |
| `GET /pedidos` (todos) | nao | sim | sim |
| `GET /pedidos/cliente/{id}` | sim | sim | sim |
| `PUT /pedidos/{id}` | sim (proprio) | nao | sim |
| `PATCH /pedidos/{id}/cancelar` | sim (proprio) | nao | sim |
| `PATCH /pedidos/{id}/avaliar` | nao | sim | sim |
| `POST /contratos` | nao | sim | sim |
| `GET /contratos` | nao | sim | sim |

> **Regra de propriedade:** CLIENTE so pode modificar ou cancelar pedidos criados com o seu proprio CPF. O sistema valida isso no servidor comparando o CPF do JWT com o `clienteId` do pedido.

### Papeis Disponiveis

| Enum `Perfil` | Descricao |
|---------------|-----------|
| `CLIENTE` | Usuario final que solicita alugueis |
| `AGENTE` | Empresa ou banco que avalia pedidos e executa contratos |
| `ADMIN` | Administrador com acesso irrestrito ao sistema |

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
    private final IClienteService clienteService;   // Interface, nao impl
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
| **Provider** | Comportamental | `AuthenticationProviderImpl` encapsula a logica de autenticacao JWT |

---

## Stack Tecnologica

### Backend

| Tecnologia | Versao | Proposito |
|------------|--------|-----------|
| Java | 17+ | Linguagem principal |
| Micronaut | 4.10 | Framework HTTP (Netty) |
| Micronaut Security JWT | 4.x | Autenticacao e autorizacao com JWT |
| Micronaut Data JPA | 4.x | Persistencia com Hibernate 6 |
| PostgreSQL | 15+ | Banco relacional (local via Docker ou nuvem via Render) |
| BCrypt (jbcrypt) | 0.4 | Hash seguro de senhas |
| OpenAPI / Swagger UI | 3.x | Documentacao interativa da API |
| JUnit 5 + Mockito | - | Testes unitarios e de integracao |
| Maven | 3.9+ | Build e gerenciamento de dependencias |

### Frontend

| Tecnologia | Versao | Proposito |
|------------|--------|-----------|
| React | 19 | Biblioteca de UI |
| TypeScript | 5.9 | Tipagem estatica |
| Vite | 8.x | Build tool e dev server |
| Axios | 1.14 | Cliente HTTP com interceptor JWT |
| React Router | 7.x | Roteamento SPA com rotas protegidas |
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
| **Usuario** | id, cpf, senha (BCrypt), perfil (CLIENTE/AGENTE/ADMIN) |

### Enumeradores

| Enum | Valores |
|------|---------|
| `StatusPedido` | PENDENTE, EM_ANALISE, APROVADO, REPROVADO, CONTRATADO, CANCELADO |
| `TipoPropriedade` | CLIENTE, EMPRESA, BANCO |
| `Perfil` | CLIENTE, AGENTE, ADMIN |

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
       │ cancelamento (somente pelo dono do pedido)
       ▼
  ┌───────────┐
  │ CANCELADO │
  └───────────┘
```

---

## API Endpoints

Documentacao interativa disponivel em `http://localhost:8080/swagger-ui/index.html`

### Autenticacao `/api/v1/auth`

| Metodo | Rota | Descricao | Acesso |
|--------|------|-----------|--------|
| `POST` | `/registro` | Criar conta de usuario | Publico |
| `POST` | `/login` | Autenticar e obter JWT | Publico |

### Clientes `/api/v1/clientes`

| Metodo | Rota | Descricao | Acesso |
|--------|------|-----------|--------|
| `POST` | `/` | Cadastrar cliente | Autenticado |
| `GET` | `/` | Listar todos | AGENTE, ADMIN |
| `GET` | `/{id}` | Buscar por ID | Autenticado |
| `GET` | `/cpf/{cpf}` | Buscar por CPF | Autenticado |
| `PUT` | `/{id}` | Atualizar | Autenticado |
| `DELETE` | `/{id}` | Remover | ADMIN |

### Automoveis `/api/v1/automoveis`

| Metodo | Rota | Descricao | Acesso |
|--------|------|-----------|--------|
| `POST` | `/` | Cadastrar automovel | ADMIN |
| `GET` | `/` | Listar todos | AGENTE, ADMIN |
| `GET` | `/disponiveis` | Listar disponiveis para aluguel | Autenticado |
| `GET` | `/{id}` | Buscar por ID | Autenticado |
| `PUT` | `/{id}` | Atualizar | ADMIN |
| `DELETE` | `/{id}` | Remover | ADMIN |

### Pedidos `/api/v1/pedidos`

| Metodo | Rota | Descricao | Acesso |
|--------|------|-----------|--------|
| `POST` | `/` | Criar pedido de aluguel | CLIENTE (auto-CPF), ADMIN |
| `GET` | `/` | Listar todos | AGENTE, ADMIN |
| `GET` | `/{id}` | Buscar por ID | Autenticado |
| `GET` | `/cliente/{clienteId}` | Listar pedidos de um cliente | Autenticado |
| `PUT` | `/{id}` | Modificar pedido pendente | CLIENTE (proprio), ADMIN |
| `PATCH` | `/{id}/avaliar` | Emitir parecer financeiro | AGENTE, ADMIN |
| `PATCH` | `/{id}/cancelar` | Cancelar pedido | CLIENTE (proprio), ADMIN |

### Contratos `/api/v1/contratos`

| Metodo | Rota | Descricao | Acesso |
|--------|------|-----------|--------|
| `POST` | `/` | Executar contrato de credito | AGENTE, ADMIN |
| `GET` | `/` | Listar todos | AGENTE, ADMIN |
| `GET` | `/{id}` | Buscar por ID | Autenticado |
| `GET` | `/pedido/{pedidoId}` | Buscar contrato por pedido | Autenticado |

---

## Estrutura do Projeto

### Backend

```
src/main/java/com/pucminas/aluguelcarros/
├── Application.java
├── application/
│   ├── controller/
│   │   ├── AuthController.java
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
│   │   │   ├── ContratoRequestDTO.java
│   │   │   └── RegistroRequestDTO.java
│   │   └── response/
│   │       ├── ClienteResponseDTO.java
│   │       ├── AutomovelResponseDTO.java
│   │       ├── PedidoResponseDTO.java
│   │       ├── ContratoResponseDTO.java
│   │       └── UsuarioResponseDTO.java
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
│   │   ├── Contrato.java
│   │   └── Usuario.java
│   ├── enums/
│   │   ├── StatusPedido.java
│   │   ├── TipoPropriedade.java
│   │   └── Perfil.java
│   └── exception/
│       ├── BusinessException.java
│       └── ResourceNotFoundException.java
└── infrastructure/
    ├── repository/
    │   ├── ClienteRepository.java
    │   ├── AutomovelRepository.java
    │   ├── PedidoRepository.java
    │   ├── ContratoRepository.java
    │   └── UsuarioRepository.java
    └── security/
        ├── AuthenticationProviderImpl.java
        └── PasswordEncoderService.java
```

### Frontend

```
frontend/src/
├── contexts/
│   └── AuthContext.tsx          (estado global de autenticacao + JWT)
├── components/
│   ├── Header.tsx               (navegacao com menu por perfil)
│   ├── PrivateRoute.tsx         (protecao de rotas por papel)
│   ├── ClienteCard.tsx
│   ├── ClienteForm.tsx
│   ├── AutomovelCard.tsx
│   ├── AutomovelForm.tsx
│   ├── PedidoCard.tsx
│   ├── ParecerModal.tsx
│   └── ConfirmModal.tsx
├── pages/
│   ├── Home.tsx                 (dashboard por perfil)
│   ├── LoginPage.tsx
│   ├── ClienteListPage.tsx
│   ├── ClienteCreatePage.tsx
│   ├── ClienteEditPage.tsx
│   ├── ClienteSearchPage.tsx
│   ├── AutomovelListPage.tsx
│   ├── AutomovelCreatePage.tsx
│   ├── AutomovelEditPage.tsx
│   ├── PedidoListPage.tsx
│   ├── PedidoCreatePage.tsx
│   ├── ContratoListPage.tsx
│   └── ContratoCreatePage.tsx
├── services/
│   └── api.ts                   (axios + interceptor JWT)
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

**Opcao 1 — Banco na nuvem (Render):**

```powershell
$env:JAVA_HOME   = "C:\Program Files\Java\jdk-17"
$env:URL_BD      = "jdbc:postgresql://dpg-d7c3t1dckfvc73833p90-a.oregon-postgres.render.com:5432/aluguelveiculo"
$env:USER_BD     = "aluguelveiculo_user"
$env:PASSWORD_BD = "98RRwDC6wkHCawSEEgB6aqKJn0toZ0It"
.\mvnw.cmd mn:run
```

**Opcao 2 — Banco local (Docker):**

```bash
cd docker-compose-postgre && docker compose up -d
```

```powershell
$env:URL_BD      = "jdbc:postgresql://localhost:5433/aluguelcarros"
$env:USER_BD     = "postgres"
$env:PASSWORD_BD = "1234567"
.\mvnw.cmd mn:run
```

Inicia na porta `8080`. Swagger UI disponivel em `http://localhost:8080/swagger-ui/index.html`.

### Criar usuarios iniciais

Apos subir o backend, registre os usuarios via `POST /api/v1/auth/registro`:

```json
{ "cpf": "33333333333", "senha": "123456", "perfil": "ADMIN"  }
{ "cpf": "22222222222", "senha": "123456", "perfil": "AGENTE" }
{ "cpf": "11111111111", "senha": "123456", "perfil": "CLIENTE" }
```

### Usuarios de teste pre-cadastrados (banco Render)

| Perfil | CPF | Senha |
|--------|-----|-------|
| ADMIN  | `33333333333` | `123456` |
| AGENTE | `22222222222` | `123456` |
| CLIENTE | `44444444444` | `senha123` |
| CLIENTE | `55555555555` | `senha123` |
| CLIENTE | `66666666666` | `senha123` |
| CLIENTE | `77777777777` | `senha123` |
| CLIENTE | `88888888888` | `senha123` |
| CLIENTE | `99999999999` | `senha123` |
| CLIENTE | `10101010101` | `senha123` |
| CLIENTE | `12121212121` | `senha123` |
| CLIENTE | `13131313131` | `senha123` |
| CLIENTE | `14141414141` | `senha123` |

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Inicia na porta `5173` com proxy configurado para o backend em `8080`.

### Testes

```bash
.\mvnw.cmd test
```

---

## Diagramas UML

Disponiveis no diretorio [`/docs`](docs/).

| Diagrama | Sprint | Arquivo |
|----------|--------|---------|
| Casos de Uso | S01 | [diagrama_casos_uso.png](docs/diagrama_casos_uso.png) |
| Classes | S01 | [diagrama_classes.png](docs/diagrama_classes.png) |
| Pacotes (Visao Logica) | S01 | [diagrama_pacotes.png](docs/diagrama_pacotes.png) |
| Componentes | S02 | [diagrama_componentes.png](docs/diagrama_componentes.png) |
| Implantacao | S03 | [diagrama-implantacao.png](docs/diagrama-implantacao.png) |

---

## Historias de Usuario

Documentadas em [`HISTORIAS_USUARIO.md`](HISTORIAS_USUARIO.md), cobrindo:

| Grupo | Historias | Descricao |
|-------|-----------|-----------|
| Cadastro e Autenticacao | US01 - US03 | Registro de clientes e acesso ao sistema por perfil |
| Fluxo de Aluguel | US04 - US06 | Solicitacao, gestao e acompanhamento de pedidos pelo cliente |
| Gestao e Aprovacao | US07 - US09 | Avaliacao financeira e execucao de contratos pelo agente |
| Financiamento | US10 - US11 | Concessao e associacao de contratos de credito bancario |
| Gestao de Frota | US12 | Manutencao dos dados de automoveis pelo administrador |

---

## Sprints

| Sprint | Entregaveis |
|--------|-------------|
| **Lab02S01** | Diagrama de Casos de Uso, Historias de Usuario, Diagrama de Classes, Diagrama de Pacotes |
| **Lab02S02** | Revisao dos diagramas + Diagrama de Componentes + CRUD completo de Cliente, Automovel, Pedido e Contrato (web, Java, MVC) |
| **Lab02S03** | Revisao dos diagramas + Diagrama de Implantacao + Autenticacao JWT + Controle de acesso por papel (CLIENTE/AGENTE/ADMIN) + Prototipo completo com ciclo de vida do pedido de aluguel |

---

<p align="center">
  <strong>PUC Minas</strong> — Laboratorio de Desenvolvimento de Software
</p>
