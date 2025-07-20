FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/price-feed-mock.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
