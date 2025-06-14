# Etapa 1: Construcción del proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java","-jar","app.jar"]
