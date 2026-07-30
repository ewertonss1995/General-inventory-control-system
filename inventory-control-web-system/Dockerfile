# Estágio 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Estágio 2: Runtime leve (Alpine Linux)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cria o grupo e o usuário não-root usando a sintaxe do Alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia o JAR compilado atribuindo a propriedade diretamente ao usuário não-root
COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

# Alterna para o usuário não-root com segurança
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]