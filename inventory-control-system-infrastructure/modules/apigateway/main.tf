resource "aws_apigatewayv2_api" "api_gateway" {
  name          = "${var.project_name}-api-gateway"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = ["*"]
    allow_methods = ["GET", "POST", "PUT", "DELETE", "PATCH"]
    allow_headers = ["*"]
  }

  tags = {
    Name = "${var.project_name}-api-gateway"
  }
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.api_gateway.id
  name        = "$default"
  auto_deploy = true
}

output "api_gateway_endpoint" {
  value       = aws_apigatewayv2_api.api_gateway.api_endpoint
  description = "URL base do API Gateway"
}

output "api_gateway_id" {
  value = aws_apigatewayv2_api.api_gateway.id
}