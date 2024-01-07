FROM openjdk:21-jdk-slim-buster
MAINTAINER vinayakmp007
COPY  target/LoggerExample-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTIONS -Dmicroservicelogger.machine_name=$HOSTNAME -jar /app/app.jar


