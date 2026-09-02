# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies separately from source so code edits don't re-download the world
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# --- Run stage ---
# Using the Ubuntu/Jammy-based Temurin image, not -alpine: the alpine tag's
# multi-arch manifest has been unreliable on some Apple Silicon Docker Desktop
# setups ("no match for platform in manifest"). Jammy is larger but has
# consistent amd64+arm64 support.
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as non-root
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
