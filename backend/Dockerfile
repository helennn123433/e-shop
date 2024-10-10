# Используем более легкий базовый образ Java 8
FROM openjdk:8-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл jar
COPY target/E-Shop-0.0.1-SNAPSHOT.jar app.jar

# Определяем переменные среды
ENV SPRING_PROFILES_ACTIVE=docker

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

# Открываем порт
EXPOSE 8080
