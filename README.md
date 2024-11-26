# README: Projeto de Gestão de E-commerce e Cartões de Crédito Trabalho de Big Data e Cloud Computing

O aluno de Luis Carlos Pastura Macedo e Guilherme Barros de Mello Almeida, 

Este projeto consiste em um sistema para gerenciar transações, pedidos, produtos e clientes em um ambiente de e-commerce. Ele utiliza uma arquitetura de microserviços com componentes em **Java** e **Node.js**, integrando-se ao CosmosDB da Azure para persistência e utilizando bibliotecas para validação e mapeamento de dados.

## Sumário
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Instalação e Configuração](#instalação-e-configuração)
- [Funcionalidades Principais](#funcionalidades-principais)
- [APIs Disponíveis](#apis-disponíveis)
- [Bot de Atendimento](#bot-de-atendimento)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## Estrutura do Projeto

### Módulo de E-commerce
**Linguagem:** Java  
- **Entidades**:
  - `Order`: Representa um pedido, armazenado no CosmosDB.
  - `Produto`: Representa um produto disponível no catálogo.

- **Validações**:
  - Validação de campos obrigatórios, como nomes de produtos e categorias.
  - Uso de anotações do Jakarta Validation para garantir consistência nos dados.

### Módulo de Cartões de Crédito
**Linguagem:** Java  
- **Entidades**:
  - `Cliente`: Representa o cliente do sistema, com informações como CPF, e-mail e endereço.
  - `Transacao`: Registra uma transação financeira associada a um cliente.

- **Relacionamentos**:
  - Um cliente pode ter múltiplos cartões e notificações.
  - Implementação de associações `OneToMany` para gestão de dados.

### Bot de Atendimento
**Linguagem:** Node.js  
- **Arquitetura**:
  - Implementado com a biblioteca **botbuilder-dialogs**.
  - Dialogs configurados para interagir com o usuário e fornecer informações sobre pedidos, extratos e produtos.

- **Diálogos**:
  - `Pedido`: Consulta e detalha pedidos do cliente.
  - `Extrato`: Fornece informações financeiras.
  - `Produto`: Detalha produtos cadastrados.

---

## Tecnologias Utilizadas

### Backend
- **Java**: Utilizado para a lógica de negócios do e-commerce e
