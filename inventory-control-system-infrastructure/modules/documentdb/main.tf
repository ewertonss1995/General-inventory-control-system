# Grupo de Subnets para o DocumentDB (Subnets Privadas)
resource "aws_docdb_subnet_group" "docdb_subnet_group" {
  name       = "${var.project_name}-docdb-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = { Name = "${var.project_name}-docdb-subnet-group" }
}

# Cluster do DocumentDB
resource "aws_docdb_cluster" "docdb" {
  cluster_identifier      = "${var.project_name}-docdb-cluster"
  engine                  = "docdb"
  master_username         = "db_inventory_mongo_admin"
  master_password         = "SenhaSuperSeguraMongo123!" # Altere para produção
  backup_retention_period = 5
  preferred_backup_window = "07:00-09:00"
  skip_final_snapshot     = true

  db_subnet_group_name   = aws_docdb_subnet_group.docdb_subnet_group.name
  vpc_security_group_ids = [var.docdb_security_group_id]

  tags = { Name = "${var.project_name}-docdb-cluster" }
}

# Instância única do Cluster (Para estudo/dev)
resource "aws_docdb_cluster_instance" "cluster_instances" {
  count              = 1
  identifier         = "${var.project_name}-docdb-instance-${count.index}"
  cluster_identifier = aws_docdb_cluster.docdb.id
  instance_class     = "db.t3.medium" # Instância básica do DocumentDB

  tags = { Name = "${var.project_name}-docdb-instance" }
}