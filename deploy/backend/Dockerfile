FROM openjdk:17-jdk-alpine
ARG JAR_FILE='build/libs/telegram-0.0.3c-SNAPSHOT.jar'
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]