# Inventory Control - Cloud Infrastructure 🚀

Este repositório contém a Infraestrutura como Código (IaC) para o ecossistema de controle de estoque utilizando **Terraform** na **AWS**.

## 📌 Arquitetura de Rede Criada
* **1 VPC** (`10.0.0.0/16`) para isolamento total dos recursos.
* **2 Subnets Públicas** (Distribuídas em Multi-AZ: `us-east-1a` e `us-east-1b`) para receber tráfego externo (Internet Gateway).
* **2 Subnets Privadas** (Isoladas do mundo externo) para garantir a segurança dos microsserviços e bancos de dados.

## 🛠️ Como Executar o Projeto

1. Instale o Terraform CLI e configure suas credenciais AWS (`aws configure`).
2. Inicialize os plugins do Terraform:
   ```bash
   terraform init

## 🔒 Camada de Segurança e Banco de Dados (RDS)
* **Firewall Isolado (Security Groups):** O banco de dados SQL Server rejeita conexões de qualquer IP externo. Ele aceita tráfego exclusivamente originado das subnets do Kubernetes através da porta `1433`.
* **RDS SQL Server Web Edition:** Provisionado em subnets privadas distribuídas em Multi-AZ, utilizando armazenamento moderno SSD GP3 de alta performance de IOPS.

* **Amazon DocumentDB (MongoDB-compatible):** Nosso banco NoSQL para o catálogo flexível de produtos. Ele roda isolado nas subnets privadas e aceita conexões unicamente vindas do Kubernetes na porta `27017` por meio de criptografia TLS em trânsito.

* **Amazon MSK (Apache Kafka):** Nosso barramento de mensageria para a comunicação assíncrona entre o `inventory-control-web-system` (BFF) e o `inventory-control-system` (Core API). Ele está provisionado com 2 brokers de alta disponibilidade (Multi-AZ) nas nossas subnets privadas, aceitando conexões nas portas `9092` (Plaintext) e `9094` (TLS).