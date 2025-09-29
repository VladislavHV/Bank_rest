# Bank REST API

REST-сервис для управления банковскими картами с безопасной аутентификацией, ролевым доступом и возможностью перевода средств между картами.

## Описание задачи

### Функционал:

- Создание и управление банковскими картами
- Просмотр карт (с фильтрацией и пагинацией)
- Переводы между своими картами

### Атрибуты карты:

- Номер карты (зашифрован, отображается как `**** **** **** 1234`)
- Владелец
- Срок действия
- Статус: Активна / Заблокирована / Истек срок
- Баланс

## Аутентификация и роли

### Используемые технологии:

- `Spring Security + JWT`
- Роли:
  - `ADMIN`: полный доступ ко всем картам и пользователям
  - `USER`: доступ только к своим картам

## Возможности

### Администратор:

- Создаёт, блокирует, активирует, удаляет карты
- Управляет пользователями
- Видит все карты (пагинация)

### Пользователь:

- Просматривает свои карты
- Запрашивает блокировку карты
- Делает переводы между своими картами
- Смотрит баланс

---

## Технологии

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL / MySQL
- Liquibase
- Docker (dev-среда)
- Swagger / OpenAPI

---

## Структура проекта

src/

├── config # JWT, Security, конфигурации

├── controller # REST API контроллеры

├── dto # Объекты запроса/ответа

├── entity # JPA сущности

├── repository # Репозитории JPA

├── service # Сервисный слой

├── exception # Обработка ошибок

└── db/migration # Liquibase changelogs

## Быстрый старт

## 1. Клонировать проект

git clone https://github.com/VladislavHV/Bank_rest.git
cd bank_rest

## 2. Создать базу данных:
CREATE DATABASE bank_db;


Или через Docker:

docker-compose up -d

## 3. Настроить application.yml или .env:
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bank_db
    username: your_username
    password: your_password

  jpa:
    hibernate:
      ddl-auto: validate

  liquibase:
    enabled: true
    change-log: classpath:db/migration/master-changelog.yml

jwt:
  secret: your_jwt_secret_key
  expiration: 86400000

## 4. Запустить приложение:
./mvnw spring-boot:run

## 5. Swagger UI:
http://localhost:8080/swagger-ui/index.html

## API (примеры)
Регистрация
POST /auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "pass",
  "role": "USER"
}

Логин
POST /auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "pass"
}


В ответе приходит JWT, передавайте его в Authorization: Bearer <token>

## Работа с картами
 Пользователь (ROLE_USER):
Метод	URI	Описание
GET	  /cards	                  Получить свои карты
POST	/cards	                  Создать новую карту
PUT	  /cards/{id}/toggle-status	Заблокировать/разблокировать карту
POST	/cards/transfer	          Перевод между своими картами
 Администратор (ROLE_ADMIN):
Метод	URI	Описание
GET	    /cards/all?page=0&size=10	   Все карты (пагинация)
DELETE	/cards/{id}	                  Удалить карту
PUT	    /cards/{id}/toggle-status	    Заблокировать/активировать карту

## Безопасность

JWT-токены и Spring Security
Ролевой доступ
Маскирование номеров карт
Шифрование CVV и других чувствительных данных

## Документация

Swagger UI: /swagger-ui/index.html
OpenAPI Spec: docs/openapi.yaml
Liquibase миграции: src/main/resources/db/migration

## Тестирование

Юнит-тесты покрывают ключевые методы бизнес-логики (BankCardService)
Запуск тестов:

./mvnw test

## Docker (опционально)

version: "3.8"
services:
  db:
    image: postgres:15
    restart: always
    environment:
      POSTGRES_DB: bank_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"


