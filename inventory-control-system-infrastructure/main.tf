module "network" {
  source       = "./modules/vpc"
  aws_region   = var.aws_region
  project_name = var.project_name
}

module "security" {
  source       = "./modules/security"
  vpc_id       = module.network.vpc_id
  project_name = var.project_name
}

module "database" {
  source               = "./modules/rds"
  project_name         = var.project_name
  private_subnet_ids   = module.network.private_subnets
  db_security_group_id = module.security.db_sg_id
}

module "nosql_database" {
  source                  = "./modules/documentdb"
  project_name            = var.project_name
  private_subnet_ids      = module.network.private_subnets
  docdb_security_group_id = module.security.docdb_sg_id
}

module "messaging" {
  source                  = "./modules/msk"
  project_name            = var.project_name
  private_subnet_ids      = module.network.private_subnets
  kafka_security_group_id = module.security.kafka_sg_id
}