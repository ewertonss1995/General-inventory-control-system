-- Criação da tabela de usuários
CREATE TABLE users (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT PK_users PRIMARY KEY (id),
    CONSTRAINT UQ_users_username UNIQUE (username),
    CONSTRAINT UQ_users_email UNIQUE (email)
);

-- Tabela de Perfis/Regras de Acesso
CREATE TABLE roles (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(30) NOT NULL,
    
    CONSTRAINT PK_roles PRIMARY KEY (id),
    CONSTRAINT UQ_roles_name UNIQUE (name)
);

-- Associação N:N entre Usuários e Perfis
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    
    CONSTRAINT PK_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Carga inicial de Perfis do Sistema de Estoque
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_MANAGER');
INSERT INTO roles (name) VALUES ('ROLE_OPERATOR');
