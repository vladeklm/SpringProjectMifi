FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
COPY hotel-service/pom.xml ./hotel-service/

WORKDIR /build/hotel-service
RUN mvn dependency:go-offline -B

# ИСПРАВЛЕНО
COPY hotel-service/src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/hotel-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]