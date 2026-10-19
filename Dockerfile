# ---------- Etapa 1: build com Maven + JDK 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---------- Etapa 2: imagem final apenas com o JRE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/rocket-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
