# 📋 Histórias de Usuário - Sistema de Aluguel de Carros

Este documento descreve as funcionalidades do sistema sob a ótica dos usuários, seguindo os requisitos levantados e o Diagrama de Casos de Uso.

## 👥 1. Atores do Sistema

| Ator | Descrição |
|------|-----------|
| Cliente | Usuário que deseja alugar um veículo. |
| Empresa | Agente responsável por gerir a frota e validar pedidos. |
| Banco | Agente financeiro que concede crédito para os aluguéis. |
| Admin | Responsável pela manutenção dos dados base do sistema. |

## 🚗 2. Histórias de Usuário (User Stories)

### 🔹 Cadastro e Autenticação

**US01 - Cadastro de Cliente:** Como Cliente, eu quero me cadastrar informando meus dados (RG, CPF, Nome, Endereço, Profissão e até 3 Rendimentos) para que eu possa utilizar os serviços do sistema.

**US02 - Acesso ao Sistema:** Como Usuário, eu quero fazer login com meu nome de usuário e senha para acessar as funcionalidades restritas ao meu perfil.

**US03 - Gestão de Administradores:** Como Admin, eu quero manter os dados de outros administradores para garantir que apenas pessoas autorizadas gerenciem o sistema.

### 🔹 Fluxo de Aluguel (Visão do Cliente)

**US04 - Solicitação de Aluguel:** Como Cliente, eu quero introduzir um pedido de aluguel indicando o automóvel desejado para que o pedido entre em análise.

**US05 - Gestão de Pedidos Pessoais:** Como Cliente, eu quero consultar, modificar ou cancelar meus pedidos de aluguel para ter controle sobre minhas reservas pendentes.

**US06 - Acompanhamento:** Como Cliente, eu quero consultar o status do meu pedido para saber se ele já recebeu o parecer financeiro.

### 🔹 Gestão e Aprovação (Visão da Empresa)

**US07 - Modificação de Pedidos:** Como Empresa, eu quero poder modificar detalhes de pedidos de aluguel para ajustar condições contratuais antes da aprovação.

**US08 - Avaliação e Parecer:** Como Empresa, eu quero avaliar pedidos e emitir um parecer financeiro para decidir se o aluguel pode prosseguir para o contrato.

**US09 - Execução de Contrato:** Como Empresa, eu quero encaminhar pedidos aprovados para a execução do contrato para finalizar o processo de locação.

### 🔹 Financiamento e Crédito (Visão do Banco)

**US10 - Concessão de Crédito:** Como Banco, eu quero conceder contratos de crédito aos clientes para viabilizar financeiramente o aluguel do veículo.

**US11 - Associação de Contrato:** Como Banco, eu quero associar formalmente o contrato de crédito ao aluguel solicitado para fins de registro e cobrança.

### 🔹 Gestão de Ativos

**US12 - Manutenção de Frota:** Como Admin, eu quero manter os dados dos automóveis (Matrícula, Ano, Marca, Modelo e Placa) para que o inventário de aluguel esteja sempre atualizado.