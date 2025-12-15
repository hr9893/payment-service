# ===========================
# STAGE 1 - BUILD STAGE
# ===========================
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build the Spring Boot jar
RUN mvn -B -DskipTests package

# ===========================
# STAGE 2 - RUNTIME IMAGE
# ===========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose your application port
EXPOSE 8082

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]