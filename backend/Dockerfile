# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Strip Windows CRLF line endings so the script runs on Linux
RUN sed -i 's/\r$//' mvnw .mvn/wrapper/maven-wrapper.properties \
    && chmod +x mvnw \
    && ./mvnw dependency:go-offline -q
COPY src ./src
RUN ./mvnw package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
