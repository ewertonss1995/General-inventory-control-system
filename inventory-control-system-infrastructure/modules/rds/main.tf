# Grupo de Subnets do Banco: Diz ao RDS para espalhar o banco nas áreas privadas da rede
resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "${var.project_name}-rds-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = { 
    Name = "${var.project_name}-rds-subnet-group" 
  }
}

# Instância do PostgreSQL (Substituindo o SQL Server)
resource "aws_db_instance" "postgres" {
  identifier        = "${var.project_name}-postgres"
  engine            = "postgres"
  engine_version    = "15.7"            # Versão estável do PostgreSQL 15
  instance_class    = "db.t3.micro"       # db.t3.micro é suficiente para dev e entra na camada gratuita se aplicável
  allocated_storage = 20                 # 20 GB de armazenamento
  storage_type      = "gp3"
  port              = 5432                # Porta padrão do PostgreSQL

  # Nome do banco de dados inicial a ser criado (opcional)
  db_name  = "inventory_db"

  # Credenciais do Administrador (Em produção usaríamos AWS Secrets Manager)
  username = "db_inventory_admin"
  password = "SenhaSuperSegura123!" # Altere em produção

  # Associação com a Rede e Segurança
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [var.db_security_group_id]
  skip_final_snapshot    = true          # Permite destruir sem travar retenção de snapshot
  publicly_accessible     = false         # Banco 100% isolado na rede privada

  tags = { 
    Name = "${var.project_name}-postgres-instance" 
  }
}