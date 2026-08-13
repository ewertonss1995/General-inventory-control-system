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

-- Carga inicial de Usuários
INSERT INTO users (id, username, email, password_hash, active, created_at) 
VALUES ('6143a7cd-69d0-43af-8e54-88a69e22561b', 'gerente', 'gerente@exemplo.com', '$2a$10$jJ7/1Bn1dJtVvCDZWvaGVuiq/oQ6Z8LjLOb9DzqJVIO4CBRS6YZ4.', true, '2026-08-13 06:41:14.053255');

INSERT INTO users (id, username, email, password_hash, active, created_at) 
VALUES ('e01248cd-f840-4507-9ebf-431e31719e4d', 'admin', 'admin@exemplo.com', '$2a$10$qRtrhz2XtLOget/6kezfrOGLjSW3jgRf/g90CwTxjZgip/rJxHjWq', true, '2026-08-13 06:41:50.022907');

-- Carga inicial de Perfis do Sistema de Estoque
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_MANAGER');
INSERT INTO roles (name) VALUES ('ROLE_OPERATOR');


-- Carga inicial de associação entre Usuários e Perfils
INSERT INTO user_roles (user_id, role_id) 
VALUES ('6143a7cd-69d0-43af-8e54-88a69e22561b', (SELECT id FROM roles WHERE name = 'ROLE_MANAGER'));

INSERT INTO user_roles (user_id, role_id)
VALUES ('e01248cd-f840-4507-9ebf-431e31719e4d', (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));
