# ===================================================================
# Stage 1: Compila o projeto com Maven
# ===================================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependências primeiro (camada fica em cache enquanto o pom.xml não mudar)
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
# Os testes rodam no pipeline de CI; aqui só empacotamos
RUN mvn clean package -DskipTests -B

# ===================================================================
# Stage 2: Imagem final só com o JRE (bem menor que a de build)
# ===================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Roda como usuário sem privilégios de root
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
