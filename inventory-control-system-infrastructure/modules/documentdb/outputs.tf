output "docdb_endpoint" {
  value       = aws_docdb_cluster.docdb.endpoint
  description = "Endpoint do cluster DocumentDB (MongoDB)"
}
