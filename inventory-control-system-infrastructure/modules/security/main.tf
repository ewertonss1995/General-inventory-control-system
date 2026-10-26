# Security Group exclusivo para o Load Balancer (Abre as portas para o mundo)
resource "aws_security_group" "alb_sg" {
  name        = "${var.project_name}-alb-sg"
  description = "Security Group para o Application Load Balancer"
  vpc_id      = var.vpc_id

  ingress {
    description = "Permite HTTP de qualquer lugar"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Permite HTTPS de qualquer lugar"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-alb-sg" }
}

# Security Group para o Kubernetes (Simulação das instâncias EC2/EKS)
resource "aws_security_group" "eks_sg" {
  name        = "${var.project_name}-eks-sg"
  description = "Security Group para os Nodes do Kubernetes"
  vpc_id      = var.vpc_id

  # Regra de Entrada (Ingress): Permite tráfego vindo da VPC/API Gateway nas portas HTTP/HTTPS
  ingress {
    description = "Permite trafego HTTP vindo da VPC / API Gateway"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    security_groups = [aws_security_group.alb_sg.id] # <- Protegido pelo ALB!
  }

  ingress {
    description = "Permite trafego HTTPS vindo da VPC / API Gateway"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    security_groups = [aws_security_group.alb_sg.id] # <- Protegido pelo ALB!
  }

  # Libera saída de tráfego para qualquer lugar (Baixar imagens Docker, atualizar pacotes)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-eks-sg" }
}

# Security Group para o PostgreSQL (RDS)
resource "aws_security_group" "db_sg" {
  name        = "${var.project_name}-db-sg"
  description = "Security Group para o RDS PostgreSQL"
  vpc_id      = var.vpc_id

  # Regra de Entrada (Ingress): SÓ permite conexões PostgreSQL (5432) vindas do EKS
  ingress {
    description     = "Permite conexoes PostgreSQL apenas do cluster Kubernetes"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_sg.id]
  }

  # Regra de Saída
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
