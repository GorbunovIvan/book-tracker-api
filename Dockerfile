FROM openjdk:21

WORKDIR /app

COPY target/book-tracker-api-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t ivangorbunovv/book-tracker-api .'
