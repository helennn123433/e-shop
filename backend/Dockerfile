# Используем более легкий базовый образ с Java и Maven wrapper
FROM maven:3.6.3-jdk-8 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы Maven wrapper и проект
COPY . .

# Выполняем команду Maven для сборки проекта (собираем JAR)
RUN mvn clean package


# Сборка образа для запуска
FROM openjdk:8-jdk-alpine

# Копируем сгенерированный JAR-файл
COPY --from=build /app/target/E-Shop-0.0.1-SNAPSHOT.jar app.jar

# Определяем переменные среды
ENV SPRING_PROFILES_ACTIVE=docker

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

# Открываем порт
EXPOSE 8080
