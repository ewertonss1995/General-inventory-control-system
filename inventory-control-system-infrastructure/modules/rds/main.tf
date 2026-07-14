# Grupo de Subnets do Banco: Diz ao RDS para espalhar o banco nas áreas privadas da rede
resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "${var.project_name}-rds-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = { Name = "${var.project_name}-rds-subnet-group" }
}

# Instância do SQL Server
resource "aws_db_instance" "sql_server" {
  identifier        = "${var.project_name}-sqlserver"
  engine            = "sqlserver-web"
  engine_version    = "15.00.4073.23.v1" # SQL Server 2019
  instance_class    = "db.t3.small"       # Instância econômica para ambiente de desenvolvimento/estudo
  allocated_storage = 20                  # 20 GB de armazenamento em disco
  storage_type      = "gp3"

  # Credenciais do Administrador (Em produção usaríamos AWS Secrets Manager)
  username = "db_inventory_admin"
  password = "SenhaSuperSegura123!" # Altere para produção

  # Associação com a Rede e Segurança
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [var.db_security_group_id]
  skip_final_snapshot    = true # Permite destruir o laboratório com terraform destroy sem travar
  publicly_accessible    = false # GARANTIA SÊNIOR: Banco 100% isolado da internet

  tags = { Name = "${var.project_name}-rds-instance" }
}