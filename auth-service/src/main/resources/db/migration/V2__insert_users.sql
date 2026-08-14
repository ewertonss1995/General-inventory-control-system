-- Carga inicial de Usuários
INSERT INTO users (id, username, email, password_hash, active, created_at) 
VALUES ('6143a7cd-69d0-43af-8e54-88a69e22561b', 'gerente', 'gerente@exemplo.com', '$2a$10$jJ7/1Bn1dJtVvCDZWvaGVuiq/oQ6Z8LjLOb9DzqJVIO4CBRS6YZ4.', true, '2026-08-13 06:41:14.053255');

INSERT INTO users (id, username, email, password_hash, active, created_at) 
VALUES ('e01248cd-f840-4507-9ebf-431e31719e4d', 'admin', 'admin@exemplo.com', '$2a$10$qRtrhz2XtLOget/6kezfrOGLjSW3jgRf/g90CwTxjZgip/rJxHjWq', true, '2026-08-13 06:41:50.022907');

-- Carga inicial de associação entre Usuários e Perfils
INSERT INTO user_roles (user_id, role_id) 
VALUES ('6143a7cd-69d0-43af-8e54-88a69e22561b', (SELECT id FROM roles WHERE name = 'ROLE_MANAGER'));

INSERT INTO user_roles (user_id, role_id)
VALUES ('e01248cd-f840-4507-9ebf-431e31719e4d', (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));