Hotel Booking System (Microservices)
Распределённая система бронирования отелей на базе Spring Boot и Spring Cloud. Проект демонстрирует микросервисную архитектуру, паттерн Saga для обеспечения согласованности данных, JWT-авторизацию и сервисную сетку (Service Mesh) через Eureka и API Gateway.

Архитектура
Система состоит из четырех основных компонентов:

API Gateway (Port 8080): Единая точка входа, маршрутизирует запросы и проксирует токены.
Eureka Server (Port 8761): Реестр сервисов для динамического обнаружения.
Hotel Service (Port 8082): Управляет отелями, номерами и статистикой загрузки.
Booking Service (Port 8081): Управляет пользователями, бронированиями и оркестрирует процесс бронирования.
text

[Client]
|
v
[API Gateway :8080] ----> [Eureka Server :8761]
|                            ^
v                            |
[Booking Service :8081] --------+
| (WebClient / Retry / Saga)
v
[Hotel Service :8082]
Технологии
Java 17
Spring Boot 3.2.3
Spring Cloud (Eureka, Gateway, OpenFeign/WebClient)
Spring Data JPA + H2 (In-Memory DB)
Spring Security + JWT (Auth0)
Docker & Docker Compose
Swagger (OpenAPI 3.0)
Maven
Основные функции
Аутентификация: Регистрация и вход с выдачей JWT токенов. Разделение ролей USER и ADMIN.
Умное планирование: Автоматический подбор номера на основе статистики занятости (times_booked).
Распределённые транзакции (Saga):
Создание бронирования в статусе PENDING.
Блокировка слота в отеле.
Подтверждение (CONFIRMED) или отмена (CANCELLED) с компенсирующим действием (освобождение слота).
Идемпотентность: Использование requestId для предотвращения дубликатов при повторных попытках.
Повторные попытки (Retry): Настройка повторов с backoff-стратегией при вызовах между сервисами.
Установка и запуск
Предварительные требования
Установленный Docker.
Установленный Docker Compose.
Запуск
Клонирование репозитория:
bash

git clone <repository-url>
cd hotel-booking-system
Запуск контейнеров:
При первом запуске Docker загрузит зависимости Maven, соберет JAR-файлы и запустит сервисы. Это может занять несколько минут.
bash

docker compose up --build
Проверка статуса:
Eureka Server: http://localhost:8761
API Gateway: http://localhost:8080
API Документация (Swagger)
Документация доступна на портах сервисов (не через Gateway):

Booking Service: http://localhost:8081/swagger-ui/index.html
Hotel Service: http://localhost:8082/swagger-ui/index.html
Пример сценария использования
1. Регистрация пользователя (Booking Service)
   Метод: POST /user/register (через Gateway: localhost:8080/user/register)
   Body:
   json

{
"username": "admin",
"password": "admin123"
}
Результат: Вернется токен (JWT). Скопируйте его.
2. Создание отеля и номера (Hotel Service) — Только для ADMIN
   Для работы с отелями необходимы эндпойнты Swagger на порту 8082.

Создать отель: POST /api/hotels
json

{ "name": "Grand Hotel", "address": "Moscow" }
Создать номер: POST /api/rooms
json

{ "hotelId": 1, "number": "101", "available": true }
3. Создание бронирования (Booking Service)
   Используйте Swagger на 8081. Не забудьте нажать кнопку "Authorize" вверху и ввести Bearer <token>.

Создать бронирование: POST /api/bookings (через Gateway: localhost:8080/api/bookings)
Body:
json

{
"autoSelect": true,
"startDate": "2024-12-01",
"endDate": "2024-12-05"
}
Результат: Бронирование будет создано в статусе CONFIRMED.
4. Просмотр истории
   Метод: GET /api/bookings (покажет бронирования текущего пользователя).