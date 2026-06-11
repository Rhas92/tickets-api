FROM eclipse-temurin:21-jre

LABEL authors="Ramón Aller"

WORKDIR /app

COPY target/tickets-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
