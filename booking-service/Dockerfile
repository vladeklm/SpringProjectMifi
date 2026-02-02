FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
COPY booking-service/pom.xml ./booking-service/

WORKDIR /build/booking-service
RUN mvn dependency:go-offline -B

# ИСПРАВЛЕНО
COPY booking-service/src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/booking-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]