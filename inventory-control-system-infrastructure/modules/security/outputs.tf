output "eks_sg_id" { value = aws_security_group.eks_sg.id }
output "db_sg_id" { value = aws_security_group.db_sg.id }
output "docdb_sg_id" { value = aws_security_group.docdb_sg.id }
output "kafka_sg_id" { value = aws_security_group.kafka_sg.id }