# Используем официальный образ с установленной JDK 21
FROM openjdk:21-jdk-slim AS build

# Создаём рабочую директорию
WORKDIR /app

# Копируем исходники в контейнер
COPY src/ /app/src

# Компилируем исходники.
# -d out указывает, что классы будут скомпилированы в папку out
RUN javac /app/src/com/example/*.java -d out

# Вторым этапом используем образ с более лёгкой JRE (чтобы уменьшить размер финального образа)
FROM openjdk:21-slim

# Создаём рабочую директорию
WORKDIR /app

# Копируем скомпилированные .class-файлы из предыдущего этапа
COPY --from=build /app/out /app

# Запускаем программу
CMD ["java", "com.example.Main"]