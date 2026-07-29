# Estágio 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia o pom.xml e o código-fonte de uma vez para realizar o build
COPY pom.xml .
COPY src ./src

# Compila o projeto baixando as dependências de forma segura
RUN mvn package -DskipTests

# Estágio 3: Runtime leve
FROM eclipse-temurin:18-jre-alpine
WORKDIR /app

# Cria um usuário não-root para segurança do container
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
