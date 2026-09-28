# Estágio 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Estágio 2: Runtime definitivo (Java 21 no Alpine possui suporte perfeito a cgroups v2)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria usuário não-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar

# Garante a posse do arquivo app.jar para o appuser
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
