# 🚗 Sistema de Gestão de Aluguel de Carros

Projeto desenvolvido para o curso de Engenharia de Software da PUC Minas, focado na criação de um sistema web para apoio à gestão de aluguéis de automóveis.

**Desenvolvedora:** Lara Andrade, Allan Mateus, Gabriel Santiago

---

## 📋 Sobre o Projeto

O sistema permite a gestão completa do ciclo de vida de um aluguel de automóveis através da Internet. Ele atende a três perfis principais:
- **Clientes:** Podem introduzir, modificar, consultar e cancelar pedidos de aluguel.
- **Agentes (Empresas e Bancos):** Avaliam pedidos sob a ótica financeira, emitem pareceres e concedem contratos de crédito.
- **Administradores:** Gerenciam os dados base do sistema (usuários e frota).

## 🎯 Sprint Atual: Lab02S01 - Modelagem do Sistema

Esta etapa do projeto foca na especificação e design arquitetural do software, garantindo o alinhamento entre os requisitos levantados e a futura implementação. 

### Entregáveis da Sprint:
1. **Histórias de Usuário:** Detalhamento dos requisitos sob a perspectiva dos atores do sistema.
2. **Diagrama de Casos de Uso:** Mapeamento das interações entre os atores (Cliente, Empresa, Banco, Admin) e as funcionalidades do sistema.
3. **Diagrama de Classes:** Estrutura de dados e relacionamentos do domínio (Cliente, Pedido, Automóvel, etc.).
4. **Diagrama de Pacotes (Visão Lógica):** Estruturação do sistema em camadas, refletindo a arquitetura MVC que será implementada em Java.

## 🏗️ Arquitetura e Tecnologias

O sistema foi projetado para ser desenvolvido utilizando a linguagem **Java**, seguindo rigorosamente o padrão de arquitetura **MVC (Model-View-Controller)**. 

A divisão lógica do projeto está estruturada nas seguintes camadas:
- **Apresentação (View):** Construção dinâmica das páginas web.
- **Controle (Controller):** Gerenciamento das requisições web e rotas.
- **Negócio (Service/Facade):** Regras de avaliação financeira, execução de contratos e validações.
- **Persistência (Model/Repository/DAO):** Mapeamento objeto-relacional e acesso ao banco de dados.

---

## 📁 Estrutura do Repositório

* `/docs`: Contém todas as versões exportadas dos modelos UML (PNG/PDF) e arquivos fonte editáveis (ex: `.drawio`).
* `/src`: Código fonte da aplicação Java MVC (a ser alimentado nas próximas sprints).
* `Requisitos.md`: Documento de texto detalhando as Histórias de Usuário.