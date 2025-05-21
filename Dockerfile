FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=target/cityinfo-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
