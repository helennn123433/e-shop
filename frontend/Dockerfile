# Указываем базовый образ Node.js
FROM node:20

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем package.json и package-lock.json (это помогает закэшировать зависимости)
COPY package*.json ./

# Устанавливаем зависимости
RUN npm ci

# Копируем остальные файлы проекта
COPY . .

# Собираем проект для production
RUN npm run build

# Команда запуска
CMD ["npm", "run", "serve"]

# Открываем порт для доступа
EXPOSE 8080
