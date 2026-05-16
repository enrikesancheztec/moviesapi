FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./

COPY src/ src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

RUN groupadd --system appgroup && useradd --system --gid appgroup --create-home appuser

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
