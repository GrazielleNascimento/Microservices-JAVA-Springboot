FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

# Configuração do Timezone

ENV TZ=America/Sao_Paulo

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=build /app/target/mscarescheduling-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

#ENTRYPOINT ["java", "-jar", "app.jar"]
# Adiciona flags de JVM para configurar timezone

ENTRYPOINT ["java", "-Duser.timezone=America/Sao_Paulo", "-Dspring.jackson.time-zone=America/Sao_Paulo", "-Dspring.jpa.properties.hibernate.jdbc.time_zone=America/Sao_Paulo", "-jar", "app.jar"]