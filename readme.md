# senla-java-marketplace

REST API учебного маркетплейса для публикации, поиска и покупки объявлений.

> Итоговый проект корпоративного курса СЕНЛА «Java с нуля», 2024–2025.
>
> Участники проходили отбор по тестовому заданию, изучали Java на практике под руководством менторов компании и готовились к стажировке в СЕНЛА.

## Возможности

- регистрация и вход по JWT;
- управление профилем и загрузка аватара;
- создание, изменение, удаление и поиск объявлений;
- фильтрация по категории, цене и статусу, сортировка и пагинация;
- покупка за внутренний баланс, ручная отметка о продаже и история продаж;
- платное продвижение объявления;
- чаты покупателей и продавцов;
- оценки и отзывы покупателей;
- роли `USER` и `ADMIN`.

## Технологии

- Java 17;
- Spring Framework 6: MVC, Security, Transactions;
- Hibernate / JPA;
- PostgreSQL 17;
- JWT (JJWT) и BCrypt;
- MapStruct;
- JUnit 5 и Mockito;
- Maven и Apache Tomcat 11.

## Структура

```text
.
├── PlacingAdsSystem/              # Maven-модуль приложения
│   └── src/main/java/org/example/
│       ├── controller/            # HTTP-эндпоинты
│       ├── service/               # бизнес-логика
│       ├── repository/            # доступ к данным
│       ├── model/                 # JPA-сущности
│       ├── dto/ и mapper/         # API-модели и преобразования
│       └── config/, filter/       # конфигурация и JWT-фильтр
├── create_tables.sql              # схема базы данных
├── create_db.bat                  # создание БД в Windows
└── create_war.bat                 # сборка и развёртывание WAR в Tomcat
```

## Запуск

### Требования

- JDK 17;
- Maven 3.9+;
- PostgreSQL 17;
- Apache Tomcat 11 — для развёртывания WAR;
- Windows — для использования приложенных `.bat`-скриптов.

### 1. Настройка БД

Проверьте параметры подключения в [`PlacingAdsSystem/src/main/resources/application.properties`](PlacingAdsSystem/src/main/resources/application.properties). Для локального запуска PostgreSQL должен быть доступен по указанному адресу.

### 2. Создание базы данных

Добавьте путь к `psql` в переменную окружения `PATH`, затем в корне репозитория выполните:

```bat
create_db.bat
```

Скрипт создаст базу `placingadssystem` и применит [`create_tables.sql`](create_tables.sql).

### 3. Проверка и сборка

```bash
cd PlacingAdsSystem
mvn test
mvn package
```

Собранный архив будет расположен по пути `PlacingAdsSystem/target/PlacingAdsSystem.war`.

### 4. Развёртывание в Tomcat

Скопируйте WAR-файл в каталог `webapps` Apache Tomcat и перезапустите сервер. В Windows можно выполнить `create_war.bat`: он соберёт проект, запросит путь к `webapps` и перезапустит службу `Tomcat11`.

## API

Все маршруты, кроме `/auth/**`, требуют JWT в заголовке:

```http
Authorization: Bearer <JWT>
```

| Группа | Примеры маршрутов | Назначение |
| --- | --- | --- |
| Авторизация | `POST /auth/register`, `POST /auth/login` | Регистрация и получение JWT |
| Пользователь | `GET /user/profile`, `PATCH /user/update` | Профиль, аватар и история продаж |
| Объявления | `POST /ads/create`, `GET /ads/search`, `POST /ads/{id}/purchase` | Объявления, поиск и покупка |
| Чаты | `GET /chats/ads/{id}`, `POST /chats/ads/{id}` | Открытие чата и сообщения |
| Отзывы | `POST /ads/{id}/comment`, `PATCH /ads/{id}/comments/{commentId}` | Оценки и комментарии |
| Администрирование | `PATCH /admin/balance` | Пополнение внутреннего баланса |

### Пример регистрации

```http
POST /auth/register
Content-Type: application/json

{
  "username": "anna",
  "email": "anna@example.com",
  "password": "strong-password",
  "role": "USER"
}
```

Ответ содержит JWT, который следует передавать в последующих запросах.

## Тестирование

В проекте есть unit-тесты контроллеров и сервисов, написанные с использованием JUnit 5, Mockito и Spring Test.

```bash
cd PlacingAdsSystem
mvn test
```
