# Learning Platform - Платформа для онлайн обучения

Веб-приложение для управления онлайн курсами, построенное на Spring Boot с использованием Hibernate/JPA и PostgreSQL.

## Содержание
- [Требования](#требования)
- [Установка и настройка](#установка-и-настройка)
- [Запуск приложения](#запуск-приложения)
- [Структура проекта](#структура-проекта)
- [Модель данных](#модель-данных)
- [REST API](#rest-api)
- [Тестирование](#тестирование)
- [Особенности реализации](#особенности-реализации)

## Требования

- **Java**: 21 или выше
- **Gradle**: 8.14 или выше (встроенный Gradle Wrapper)
- **PostgreSQL**: 12 или выше
- **Git**: для клонирования репозитория

## Установка и настройка

### 1. Установка PostgreSQL

Скачайте и установите PostgreSQL с официального сайта: https://www.postgresql.org/download/

### 2. Создание базы данных

Подключитесь к PostgreSQL и выполните следующие команды:

```sql
CREATE DATABASE mephi;
CREATE SCHEMA IF NOT EXISTS public;
```

### 3. Настройка приложения

Приложение использует переменные окружения для конфигурации подключения к БД (файл `src/main/resources/application.yml`):

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/mephi}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update  # Hibernate автоматически создаст таблицы
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        default_schema: public
```

#### Настройка переменных окружения

Необходимо задать следующие переменные окружения:

- `DB_URL` - URL подключения к базе данных (по умолчанию: `jdbc:postgresql://localhost:5432/mephi`)
- `DB_USERNAME` - имя пользователя PostgreSQL (по умолчанию: `postgres`)
- `DB_PASSWORD` - пароль пользователя PostgreSQL (**обязательная переменная**)

**Пример для Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="1234"
```

**Пример для Windows (CMD):**
```cmd
set DB_PASSWORD=1234
```

**Пример для Linux/Mac:**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/mephi"
export DB_USERNAME="postgres"
export DB_PASSWORD="1234"
```

**Альтернатива**: Вы можете создать файл `.env` в корне проекта (см. `.env.example`) и использовать его для локальной разработки.

## Запуск приложения

**Важно**: Перед запуском убедитесь, что переменная окружения `DB_PASSWORD` установлена (см. раздел "Настройка переменных окружения").

### Вариант 1: Запуск через Gradle

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="1234"
gradlew.bat bootRun
```

**Windows (CMD):**
```cmd
set DB_PASSWORD=1234
gradlew.bat bootRun
```

**Linux/Mac:**
```bash
export DB_PASSWORD="1234"
./gradlew bootRun
```

### Вариант 2: Сборка и запуск JAR

**Windows (PowerShell):**
```powershell
# Сборка
$env:DB_PASSWORD="1234"
gradlew.bat build

# Запуск
$env:DB_PASSWORD="1234"
java -jar build/libs/learning_platform-0.0.1-SNAPSHOT.jar
```

**Linux/Mac:**
```bash
# Сборка
export DB_PASSWORD="1234"
./gradlew build

# Запуск
export DB_PASSWORD="1234"
java -jar build/libs/learning_platform-0.0.1-SNAPSHOT.jar
```

### Вариант 3: Запуск из IDE

1. Откройте проект в IntelliJ IDEA
2. Откройте **Run → Edit Configurations**
3. Выберите `LearningPlatformApplication`
4. В разделе **Environment variables** добавьте: `DB_PASSWORD=1234`
5. Нажмите **OK** и запустите приложение

Приложение запустится на порту **8080**: http://localhost:8080

## Структура проекта

```
learning_platform/
├── src/
│   ├── main/
│   │   ├── java/mephi/
│   │   │   ├── controller/        # REST контроллеры
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── CourseController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── ModuleController.java
│   │   │   │   ├── LessonController.java
│   │   │   │   ├── QuizController.java
│   │   │   │   └── TagController.java
│   │   │   ├── entity/            # JPA сущности
│   │   │   │   ├── User.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Course.java
│   │   │   │   ├── Module.java
│   │   │   │   ├── Lesson.java
│   │   │   │   ├── Assignment.java
│   │   │   │   ├── Submission.java
│   │   │   │   ├── Quiz.java
│   │   │   │   ├── Question.java
│   │   │   │   ├── AnswerOption.java
│   │   │   │   ├── QuizSubmission.java
│   │   │   │   ├── Tag.java
│   │   │   │   ├── Enrollment.java
│   │   │   │   ├── CourseReview.java
│   │   │   │   └── Profile.java
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── mapper/            # MapStruct мапперы
│   │   │   ├── repository/        # Spring Data JPA репозитории
│   │   │   ├── service/           # Бизнес-логика
│   │   │   ├── exception/         # Глобальная обработка исключений
│   │   │   └── LearningPlatformApplication.java
│   │   └── resources/
│   │       ├── application.yml          # Конфигурация (production)
│   │       └── application-test.yml     # Конфигурация для тестов (H2)
│   └── test/
│       └── java/mephi/
│           ├── controller/        # Тесты контроллеров
│           ├── repository/        # Тесты репозиториев и CASCADE операций
│           └── integration/       # Комплексные сценарии
├── build.gradle                   # Конфигурация Gradle
└── README.md
```

## Модель данных

Система содержит **15 сущностей** с различными типами связей:

### Основные сущности

1. **User** (Пользователь)
   - Роли: STUDENT, TEACHER, ADMIN
   - Связи: Profile (1:1), Courses (M:N через Enrollment), Submissions, QuizSubmissions, Reviews

2. **Category** (Категория курсов)
   - Связи: Courses (1:M)

3. **Course** (Курс)
   - Связи: Category (M:1), Teacher (M:1), Modules (1:M), Enrollments (1:M), Reviews (1:M), Tags (M:N)

4. **Module** (Модуль курса)
   - Связи: Course (M:1), Lessons (1:M), Quiz (1:1)

5. **Lesson** (Урок)
   - Связи: Module (M:1), Assignments (1:M)

6. **Assignment** (Задание)
   - Связи: Lesson (M:1), Submissions (1:M)

7. **Submission** (Решение задания)
   - Связи: Assignment (M:1), Student (M:1)

8. **Quiz** (Тест)
   - Связи: Module (1:1), Questions (1:M), QuizSubmissions (1:M)

9. **Question** (Вопрос теста)
   - Связи: Quiz (M:1), AnswerOptions (1:M)

10. **AnswerOption** (Вариант ответа)
    - Связи: Question (M:1)

11. **QuizSubmission** (Результат прохождения теста)
    - Связи: Quiz (M:1), Student (M:1)

12. **Tag** (Тег)
    - Связи: Courses (M:N)

13. **Enrollment** (Запись на курс)
    - Связи: User (M:1), Course (M:1)

14. **CourseReview** (Отзыв о курсе)
    - Связи: User (M:1), Course (M:1)

15. **Profile** (Профиль пользователя)
    - Связи: User (1:1)

### Диаграмма связей (упрощенная)

```
User ←--→ Profile (1:1)
User --→ Enrollment ←-- Course
User --→ Submission ←-- Assignment ←-- Lesson ←-- Module ←-- Course
User --→ QuizSubmission ←-- Quiz ←--(1:1)-- Module
User --→ CourseReview ←-- Course
Category --→ Course
Course ←--→ Tag (M:N)
Quiz --→ Question --→ AnswerOption
```

### Типы связей

- **One-to-One**: User ↔ Profile, Module ↔ Quiz
- **One-to-Many**: Course → Module → Lesson → Assignment → Submission
- **Many-to-One**: все обратные связи One-to-Many
- **Many-to-Many**: User ↔ Course (через Enrollment), Course ↔ Tag

### Ленивая загрузка (Lazy Loading)

Все связи сущностей настроены с `FetchType.LAZY` для демонстрации ленивой загрузки и предотвращения проблем N+1. При обращении к не загруженным связям вне транзакции возникает `LazyInitializationException` (продемонстрировано в тестах).

## REST API

### Категории (Categories)

```http
# Создать категорию
POST /categories/create
Content-Type: application/json
{
  "name": "Programming"
}

# Получить все категории
GET /categories/list

# Получить категорию по ID
GET /categories/{id}

# Обновить категорию
PUT /categories/{id}
Content-Type: application/json
{
  "id": 1,
  "name": "Updated Name"
}

# Удалить категорию
DELETE /categories/{id}
```

### Пользователи (Users)

```http
# Создать пользователя
POST /user/create
Content-Type: application/json
{
  "name": "John Doe",
  "email": "john@example.com",
  "role": "STUDENT"  # STUDENT, TEACHER, ADMIN
}

# Получить всех пользователей
GET /users

# Получить пользователя по ID
GET /user?id=1

# Обновить пользователя
PUT /user/{id}
Content-Type: application/json
{
  "name": "Updated Name",
  "email": "updated@example.com",
  "role": "TEACHER"
}

# Удалить пользователя
DELETE /user/{id}
```

### Курсы (Courses)

```http
# Создать курс
POST /courses/create
Content-Type: application/json
{
  "title": "Java Fundamentals",
  "description": "Learn Java programming",
  "duration": 30,
  "categoryId": 1,
  "teacherId": 2
}

# Получить все курсы
GET /courses/all

# Получить курсы по категории
GET /courses/list_by_category?category_id=1

# Получить студентов курса
GET /courses/users-for-course?courseId=1

# Получить отзывы о курсе
GET /courses/reviews-for-course?courseId=1

# Создать отзыв
POST /courses/create-review
Content-Type: application/json
{
  "courseId": 1,
  "userId": 3,
  "rating": 5,
  "review": "Excellent course!"
}

# Обновить курс
PUT /courses/{id}

# Удалить курс
DELETE /courses/{id}
```

### Модули (Modules)

```http
# Создать модуль
POST /modules/create
Content-Type: application/json
{
  "title": "Introduction to OOP",
  "description": "Object-Oriented Programming basics",
  "orderIndex": 1,
  "courseId": 1
}

# Получить модули курса
GET /modules/get-list?course_id=1

# Получить модуль по ID
GET /modules/{id}

# Обновить модуль
PUT /modules/{id}

# Удалить модуль
DELETE /modules/{id}
```

### Уроки (Lessons)

```http
# Создать урок
POST /api/lessons
Content-Type: application/json
{
  "title": "Variables and Data Types",
  "content": "In this lesson we'll learn...",
  "videoUrl": "https://example.com/video",
  "orderIndex": 1,
  "moduleId": 1
}

# Получить все уроки
GET /api/lessons

# Получить уроки модуля
GET /api/lessons/module/{moduleId}

# Получить урок по ID
GET /api/lessons/{id}

# Обновить урок
PUT /api/lessons/{id}

# Удалить урок
DELETE /api/lessons/{id}
```

### Задания (Assignments)

```http
# Создать задание (к уроку)
POST /api/lessons/assignments
Content-Type: application/json
{
  "title": "Exercise 1",
  "description": "Complete the following task...",
  "maxScore": 100,
  "dueDate": "2025-12-31T23:59:59",
  "lessonId": 1
}

# Получить задания урока
GET /api/lessons/{lessonId}/assignments

# Получить задание по ID
GET /api/lessons/assignments/{id}

# Обновить задание
PUT /api/lessons/assignments/{id}

# Удалить задание
DELETE /api/lessons/assignments/{id}
```

### Решения заданий (Submissions)

```http
# Отправить решение
POST /api/lessons/submissions
Content-Type: application/json
{
  "assignmentId": 1,
  "studentId": 3,
  "content": "Here is my solution..."
}

# Оценить решение (преподаватель)
PUT /api/lessons/submissions/{id}/grade?score=95&feedback=Great work!

# Получить решения по заданию
GET /api/lessons/assignments/{assignmentId}/submissions

# Получить решения студента
GET /api/lessons/students/{studentId}/submissions
```

### Тесты (Quizzes)

```http
# Создать тест
POST /api/quizzes
Content-Type: application/json
{
  "title": "Java Basics Quiz",
  "timeLimit": 30,
  "moduleId": 1
}

# Получить все тесты
GET /api/quizzes

# Получить тест по ID
GET /api/quizzes/{id}

# Получить тест модуля
GET /api/quizzes/module/{moduleId}

# Создать вопрос
POST /api/quizzes/questions
Content-Type: application/json
{
  "text": "What is Java?",
  "type": "SINGLE_CHOICE",  # SINGLE_CHOICE или MULTIPLE_CHOICE
  "quizId": 1
}

# Получить вопросы теста
GET /api/quizzes/{quizId}/questions

# Создать вариант ответа
POST /api/quizzes/options
Content-Type: application/json
{
  "text": "A programming language",
  "isCorrect": true,
  "questionId": 1
}

# Получить варианты ответа
GET /api/quizzes/questions/{questionId}/options

# Отправить результат теста
POST /api/quizzes/submissions
Content-Type: application/json
{
  "quizId": 1,
  "studentId": 3,
  "score": 85.5
}

# Получить результаты студента
GET /api/quizzes/submissions/student/{studentId}
```

### Теги (Tags)

```http
# Создать тег
POST /api/tags
Content-Type: application/json
{
  "name": "Java"
}

# Получить все теги
GET /api/tags

# Получить тег по ID
GET /api/tags/{id}

# Получить тег по имени
GET /api/tags/name/{name}

# Обновить тег
PUT /api/tags/{id}

# Удалить тег
DELETE /api/tags/{id}
```

## Тестирование

Проект содержит **61 автоматический тест**, покрывающих все требования:

### Запуск тестов

```bash
# Все тесты
./gradlew test

# Только тесты контроллеров
./gradlew test --tests "mephi.controller.*"

# Только тесты репозиториев
./gradlew test --tests "mephi.repository.*"

# Отчет о тестах
build/reports/tests/test/index.html
```

### Типы тестов

#### 1. Controller Integration Tests (36 тестов)
Тестируют REST API endpoints с использованием MockMvc:
- `CategoryControllerTest` - CRUD операции с категориями
- `CourseControllerTest` - CRUD операции с курсами
- `UserControllerTest` - CRUD операции с пользователями
- `ModuleControllerTest` - CRUD операции с модулями
- `LessonControllerTest` - уроки, задания, решения
- `QuizControllerTest` - тесты, вопросы, результаты
- `TagControllerTest` - CRUD операции с тегами

#### 2. CASCADE Operations Tests (8 тестов)
Проверяют каскадное сохранение и удаление:
- Cascade Persist: Course→Module, Module→Lesson, Lesson→Assignment, Quiz→Question
- Cascade Delete: удаление родителя удаляет детей
- Orphan Removal: удаление из коллекции удаляет entity

#### 3. Data Integrity Tests (10 тестов)
Проверяют целостность данных и связей:
- Корректность связей между сущностями
- Обновление данных (Course description, Submission score)
- Количество связанных объектов

#### 4. Lazy Loading Tests (6 тестов)
Демонстрируют ленивую загрузку:
- Проверка `Hibernate.isInitialized()` перед и после обращения
- Работа с коллекциями внутри транзакции
- ManyToOne и OneToMany ленивые связи

#### 5. Complex Scenario Tests (3 теста)
Комплексные сценарии использования:
- Полный цикл: создание курса → модули → уроки → задания → решения → тесты
- Запись нескольких студентов на курс
- Преподаватель управляет несколькими курсами

### Проверка базы данных

После запуска приложения Hibernate создаст следующие таблицы:

```sql
-- Просмотр всех таблиц
SELECT tablename FROM pg_tables WHERE schemaname = 'public';

-- Ожидаемые таблицы (15-20):
users, profile, category, courses, modules, lessons, assignments,
submissions, quizzes, questions, answer_options, quiz_submissions,
tags, enrollment, course_review, course_tags (join table)
```

### Проверка связей

```sql
-- Проверка внешних ключей
SELECT
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema='public';
```

## Особенности реализации

### 1. Lazy Loading

Все связи настроены с `fetch = FetchType.LAZY`:

```java
@OneToMany(mappedBy = "course", cascade = CascadeType.ALL,
           fetch = FetchType.LAZY, orphanRemoval = true)
private List<Module> modules = new ArrayList<>();
```

При попытке доступа к не загруженной коллекции вне транзакции возникает `LazyInitializationException` (см. `LazyLoadingTest`).

### 2. Cascade Operations

Каскадные операции позволяют автоматически сохранять/удалять связанные сущности:

```java
@OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL,
           orphanRemoval = true)
private List<Assignment> assignments = new ArrayList<>();
```

- `CascadeType.ALL` - все операции каскадируются
- `orphanRemoval = true` - удаление из коллекции удаляет entity

### 3. DTO Pattern

Использование DTO предотвращает проблемы с ленивой загрузкой:

```java
@Mapping(target = "categoryId", source = "category.id")
@Mapping(target = "teacherId", source = "teacher.id")
CourseDto toDto(Course course);
```

### 4. Global Exception Handler

`GlobalExceptionHandler` преобразует исключения в HTTP-статусы:

```java
@ExceptionHandler(EntityNotFoundException.class)
@ResponseStatus(HttpStatus.NOT_FOUND)
public ResponseEntity<String> handleEntityNotFoundException(...)
```

### 5. MapStruct для маппинга

Автоматическая генерация мапперов во время компиляции:

```java
@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto toDto(Course course);
    Course toEntity(CourseDto dto);
}
```

### 6. Spring Data JPA Repositories

Автоматическая генерация запросов по имени метода:

```java
List<Course> findByCategoryId(Long categoryId);
List<Course> findByTeacherId(Long teacherId);
```

### 7. Транзакционность

Все сервисы помечены `@Transactional` для обеспечения ACID:

```java
@Service
@Transactional
public class CourseService { ... }
```

## Примеры использования

### Пример 1: Создание курса с модулями

```bash
# 1. Создать категорию
curl -X POST http://localhost:8080/categories/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Programming"}'
# Response: {"id":1,"name":"Programming"}

# 2. Создать преподавателя
curl -X POST http://localhost:8080/user/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Dr. Smith","email":"smith@uni.com","role":"TEACHER"}'
# Response: {"id":1,"name":"Dr. Smith",...}

# 3. Создать курс
curl -X POST http://localhost:8080/courses/create \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Basics","description":"Learn Java","duration":30,"categoryId":1,"teacherId":1}'
# Response: {"id":1,"title":"Java Basics",...}

# 4. Создать модуль
curl -X POST http://localhost:8080/modules/create \
  -H "Content-Type: application/json" \
  -d '{"title":"Introduction","orderIndex":1,"courseId":1}'
```

### Пример 2: Студент проходит курс

```bash
# 1. Создать студента
curl -X POST http://localhost:8080/user/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@student.com","role":"STUDENT"}'

# 2. Создать урок
curl -X POST http://localhost:8080/api/lessons \
  -H "Content-Type: application/json" \
  -d '{"title":"Variables","content":"...","moduleId":1}'

# 3. Создать задание
curl -X POST http://localhost:8080/api/lessons/assignments \
  -H "Content-Type: application/json" \
  -d '{"title":"Exercise 1","maxScore":100,"lessonId":1}'

# 4. Отправить решение
curl -X POST http://localhost:8080/api/lessons/submissions \
  -H "Content-Type: application/json" \
  -d '{"assignmentId":1,"studentId":2,"content":"My solution"}'

# 5. Преподаватель оценивает
curl -X PUT "http://localhost:8080/api/lessons/submissions/1/grade?score=95&feedback=Excellent!"
```

### Пример 3: Тестирование

```bash
# 1. Создать тест
curl -X POST http://localhost:8080/api/quizzes \
  -H "Content-Type: application/json" \
  -d '{"title":"Quiz 1","timeLimit":30,"moduleId":1}'

# 2. Добавить вопрос
curl -X POST http://localhost:8080/api/quizzes/questions \
  -H "Content-Type: application/json" \
  -d '{"text":"What is Java?","type":"SINGLE_CHOICE","quizId":1}'

# 3. Добавить варианты ответа
curl -X POST http://localhost:8080/api/quizzes/options \
  -H "Content-Type: application/json" \
  -d '{"text":"A programming language","isCorrect":true,"questionId":1}'

# 4. Студент проходит тест
curl -X POST http://localhost:8080/api/quizzes/submissions \
  -H "Content-Type: application/json" \
  -d '{"quizId":1,"studentId":2,"score":85.5}'

# 5. Получить результаты
curl http://localhost:8080/api/quizzes/submissions/student/2
```

## Технологии

- **Spring Boot** 3.4.4
- **Spring Data JPA** - ORM и repositories
- **Hibernate** - реализация JPA
- **PostgreSQL** - production база данных
- **H2** - in-memory база для тестов
- **MapStruct** - генерация мапперов
- **Lombok** - уменьшение boilerplate кода
- **Gradle** - система сборки
- **JUnit 5** - тестирование
- **AssertJ** - assertions в тестах

## Лицензия

Учебный проект для МИФИ (NRNU MEPhI)

---

**Автор**: Ivan Zykov
**Дата**: Ноябрь 2025
