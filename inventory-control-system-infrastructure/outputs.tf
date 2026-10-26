# ==========================================
# REDE E CONECTIVIDADE
# ==========================================
output "vpc_id" {
  value       = module.network.vpc_id
  description = "ID da VPC principal do projeto"
}

# ==========================================
# PORTAS DE ENTRADA (GATEWAYS E BALANCEADORES)
# ==========================================
output "api_gateway_endpoint" {
  value       = module.api_gateway.api_gateway_endpoint
  description = "URL base do API Gateway (Ponto de contato da internet)"
}

output "alb_dns_name" {
  value       = module.load_balancer.alb_dns_name
  description = "Endereço DNS do Application Load Balancer interno"
}

# ==========================================
# KUBERNETES (EKS)
# ==========================================
output "eks_cluster_name" {
  value       = module.eks.cluster_name
  description = "Nome do cluster EKS Kubernetes"
}

output "eks_cluster_endpoint" {
  value       = module.eks.cluster_endpoint
  description = "Endpoint da API do Kubernetes para configurar o kubectl"
}

# ==========================================
# PERSISTÊNCIA E MENSAGERIA
# ==========================================
output "rds_postgres_endpoint" {
  value       = module.database.rds_endpoint
  description = "Endpoint do banco de dados relacional PostgreSQL"
}

output "documentdb_endpoint" {
  value       = module.nosql_database.docdb_endpoint
  description = "Endpoint do banco NoSQL DocumentDB (MongoDB)"
}

output "kafka_bootstrap_brokers_tls" {
  value       = module.messaging.bootstrap_brokers_tls
  description = "Strings de conexão TLS para os brokers do Apache Kafka"
}
