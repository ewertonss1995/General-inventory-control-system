variable "aws_region" {
  type        = string
  description = "Região da AWS onde a infraestrutura será criada"
  default     = "us-east-1"
}

variable "project_name" {
  type        = string
  description = "Nome do projeto para fins de tag e organização"
  default     = "inventory-control"
}