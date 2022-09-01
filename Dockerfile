# use open jdk 11 as base image
FROM openjdk:11

# create a new app folder for application files
RUN mkdir /app

COPY backend/target/app.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
