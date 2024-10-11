# Используем более легкий базовый образ с Maven и Java 8
FROM maven:3.6.3-jdk-8

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и все файлы проекта для сборки
COPY pom.xml .
COPY src ./src

# Выполняем команду Maven для сборки проекта (зависимости и сборка)
RUN mvn clean package

# После успешной сборки копируем JAR-файл
COPY target/E-Shop-0.0.1-SNAPSHOT.jar app.jar

# Определяем переменные среды
ENV SPRING_PROFILES_ACTIVE=docker

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

# Открываем порт
EXPOSE 8080
