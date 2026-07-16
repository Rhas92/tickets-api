# Build stage: compile the jar inside Docker
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

#  Run stage: JRE only + the jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/tickets-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]