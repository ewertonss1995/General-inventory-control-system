# Security Group para o Kubernetes (Simulação das instâncias EC2/EKS)
resource "aws_security_group" "eks_sg" {
  name        = "${var.project_name}-eks-sg"
  description = "Security Group para os Nodes do Kubernetes"
  vpc_id      = var.vpc_id

  # Libera saída de tráfego para qualquer lugar (Baixar imagens Docker, atualizar pacotes)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-eks-sg" }
}

# Security Group para o SQL Server (RDS)
resource "aws_security_group" "db_sg" {
  name        = "${var.project_name}-db-sg"
  description = "Security Group para o RDS SQL Server"
  vpc_id      = var.vpc_id

  # Regra de Entrada (Ingress): SÓ permite conexões vindas do Security Group do EKS
  ingress {
    description     = "Permite conexoes apenas do cluster Kubernetes"
    from_port       = 1433
    to_port         = 1433
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_sg.id]
  }

  # Bloqueia qualquer saída desnecessária do banco
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-db-sg" }
}

# Security Group para o Amazon DocumentDB (MongoDB)
resource "aws_security_group" "docdb_sg" {
  name        = "${var.project_name}-docdb-sg"
  description = "Security Group para o Amazon DocumentDB"
  vpc_id      = var.vpc_id

  # Regra de Entrada (Ingress): SÓ permite conexões vindas do Security Group do EKS
  ingress {
    description     = "Permite conexoes apenas do cluster Kubernetes"
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-docdb-sg" }
}

# Security Group para o Amazon MSK (Kafka)
resource "aws_security_group" "kafka_sg" {
  name        = "${var.project_name}-kafka-sg"
  description = "Security Group para o Amazon MSK"
  vpc_id      = var.vpc_id

  # Regra de Entrada: Permite conexões do Kubernetes na porta Plaintext (9092) e TLS (9094)
  ingress {
    description     = "Permite conexao plaintext do Kubernetes"
    from_port       = 9092
    to_port         = 9092
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_sg.id]
  }

  ingress {
    description     = "Permite conexao TLS do Kubernetes"
    from_port       = 9094
    to_port         = 9094
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_sg.id]
  }

  # Porta do ZooKeeper para coordenação interna (se aplicável ao modo utilizado)
  ingress {
    description     = "Permite conexao Zookeeper do Kubernetes se necessario"
    from_port       = 2181
    to_port         = 2181
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-kafka-sg" }
}