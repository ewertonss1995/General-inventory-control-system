output "rds_endpoint" {
  value       = aws_db_instance.postgres.endpoint
  description = "Endpoint de conexão do PostgreSQL"
}
