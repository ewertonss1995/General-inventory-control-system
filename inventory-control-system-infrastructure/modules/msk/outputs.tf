output "bootstrap_brokers_tls" {
  value       = aws_msk_cluster.kafka.bootstrap_brokers_tls
  description = "Connection string para brokers usando TLS"
}

output "bootstrap_brokers" {
  value       = aws_msk_cluster.kafka.bootstrap_brokers
  description = "Connection string para brokers em texto plano (se habilitado)"
}