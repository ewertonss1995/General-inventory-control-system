# Criação do Cluster Apache Kafka (MSK)
resource "aws_msk_cluster" "kafka" {
  cluster_name           = "${var.project_name}-kafka"
  kafka_version          = "3.2.0" # Versão estável e amplamente utilizada do Kafka
  number_of_broker_nodes = 2

  broker_node_group_info {
    instance_type = "kafka.t3.small" # Instância ideal e econômica para ambiente de estudo/dev
    client_subnets = var.private_subnet_ids
    security_groups = [var.kafka_security_group_id]

    storage_info {
      ebs_storage_info {
        volume_size = 10 # 10 GB de armazenamento por broker
      }
    }
  }

  encryption_info {
    encryption_in_transit {
      client_in_transit = "TLS_PLAINTEXT" # Permite conexões com ou sem TLS para facilitar os testes de estudo
    }
  }

  tags = { Name = "${var.project_name}-kafka-cluster" }
}