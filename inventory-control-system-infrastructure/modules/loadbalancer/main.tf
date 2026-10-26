# 2. O Recurso do Application Load Balancer (ALB)
resource "aws_lb" "main_alb" {
  name               = "${var.project_name}-alb"
  internal           = true # true significa que ele é privado (Não aceita internet)
  load_balancer_type = "application"
  
  # O ALB precisa de um Security Group próprio
  security_groups    = var.alb_security_group_id
  
  # Em quais subnets da sua VPC o ALB vai ficar posicionado (exigência da AWS)
  subnets            = var.private_subnet_ids

  tags = { Name = "${var.project_name}-alb" }
}
