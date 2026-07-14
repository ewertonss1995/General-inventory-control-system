# 🔐 Auth Service (Microservice)

O `auth-service` é o microsserviço responsável pela **Autenticação, Autorização e Gestão de Operadores** do ecossistema de Controle de Estoque. Ele atua como um servidor de identidade de alto desempenho, emitindo tokens de acesso assinados de forma assimétrica e estruturando erros em conformidade com as melhores práticas de mercado.

---

## 🛠️ Stack Tecnológica

* **Java 17** (LTS)
* **Spring Boot 3.3.x**
* **Spring Security 6** (OAuth2 Resource Server)
* **Spring Data JPA** & **Microsoft SQL Server**
* **Flyway** (Controle de Versão de Banco de Dados)
* **Nimbus JOSE + JWT** (Criptografia de Tokens)
* **JUnit 5** & **Mockito** (Testes Unitários)
* **OpenAPI 3 / Swagger** (Documentação Interativa)

---

## 📐 Arquitetura & Design de Código

O projeto segue princípios de **Clean Architecture** e **SOLID**, dividindo-se de forma clara em três camadas de responsabilidade:

```text
src/main/java/com/study/auth/
├── domain/                    # Regras de Negócio Puras (Exceções e Modelos de Domínio)
├── application/               # Casos de Uso (RegisterUser, AuthenticateUser, DTOs)
└── infrastructure/            # Acoplamento com Frameworks (Controllers, Configs, JPA Repositories)