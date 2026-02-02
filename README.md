Распределённая система бронирования отелей на базе Spring Boot и Spring Cloud. Проект демонстрирует микросервисную архитектуру, паттерн Saga для обеспечения согласованности данных, JWT-авторизацию и сервисную сетку (Service Mesh) через Eureka и API Gateway.

Архитектура
Система состоит из четырех основных компонентов:

API Gateway (Port 8080): Единая точка входа, маршрутизирует запросы и проксирует токены.
Eureka Server (Port 8761): Реестр сервисов для динамического обнаружения.
Hotel Service (Port 8082): Управляет отелями, номерами и статистикой загрузки.
Booking Service (Port 8081): Управляет пользователями, бронированиями и оркестрирует процесс бронирования

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

Запуск
docker compose up --build

Проверка статуса:
Eureka Server: http://localhost:8761
API Gateway: http://localhost:8080
API Документация (Swagger)
Документация доступна на портах сервисов (не через Gateway):

Booking Service: http://localhost:8081/swagger-ui/index.html
Hotel Service: http://localhost:8082/swagger-ui/index.html