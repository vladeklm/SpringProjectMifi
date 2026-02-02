FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Копируем POM файлы
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/

# Переходим в папку сервиса
WORKDIR /build/api-gateway

# Кешируем зависимости
RUN mvn dependency:go-offline -B

# ИСПРАВЛЕНО: Копируем src в текущую папку, а не в ./api-gateway/src
COPY api-gateway/src ./src

# Собираем
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]