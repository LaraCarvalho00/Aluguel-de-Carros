# ---------- Stage 1: build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o projeto
COPY . .

# Faz o build do projeto (gera o jar)
RUN mvn clean package -DskipTests


# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o jar gerado no stage anterior
COPY --from=build /app/target/aluguel-de-carros-0.1.0.jar app.jar

# Porta da aplicação
EXPOSE 8080

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]