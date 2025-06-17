FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

EXPOSE 8089

RUN mvn clean package -DskipTests

RUN ls -al /app/target

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/catsGFT-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]