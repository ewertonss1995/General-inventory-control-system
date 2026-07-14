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