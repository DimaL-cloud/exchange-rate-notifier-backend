# Exchange Rate Notifier Backend

A Spring Boot application that fetches daily exchange rates from the National Bank of Ukraine (NBU) API and sends email notifications to subscribed users.

## Features

- **Automated Exchange Rate Fetching**: Scheduled task that runs daily and on application startup
- **REST API**: Endpoints to retrieve exchange rates by currency code
- **Email Notifications**: Daily email notifications for subscribed users
- **Subscription Management**: Subscribe/unsubscribe functionality for currency rate notifications
- **Comprehensive Testing**: Unit tests for services and controllers

## Technology Stack

- Java 25
- Spring Boot 3.5.7
- Spring Data JPA
- PostgreSQL
- Spring Mail
- Lombok
- Maven
- JUnit 5 & Mockito

## Prerequisites

- Java 25 or higher
- PostgreSQL database
- SMTP server credentials (e.g., Gmail)

## Configuration

Configure the following properties in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/exchange_rate_db
    username: your-username
    password: your-password

  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

scheduler:
  exchange-rate:
    cron: "0 0 9 * * *"  # Runs daily at 9:00 AM
```

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE exchange_rate_db;
```

The application will automatically create the necessary tables on startup using Hibernate's `ddl-auto: update` setting.

## Running the Application

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package
java -jar target/backend-0.0.1.jar
```

## Running Tests

```bash
./mvnw test
```

## API Endpoints

### Get Latest Exchange Rate

```http
GET /api/rates/{currencyCode}
```

**Example:**
```bash
curl http://localhost:8080/api/rates/USD
```

**Response:**
```json
{
  "currencyCode": "USD",
  "currencyName": "Долар США",
  "rate": 42.0423,
  "exchangeDate": "2025-11-17"
}
```

### Get Exchange Rate by Date

```http
GET /api/rates/{currencyCode}/history?date={date}
```

**Example:**
```bash
curl http://localhost:8080/api/rates/USD/history?date=2025-11-17
```

### Subscribe to Currency Notifications

```http
POST /api/subscriptions
Content-Type: application/json

{
  "email": "user@example.com",
  "currencyCode": "USD"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","currencyCode":"USD"}'
```

**Response:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "currencyCode": "USD",
  "active": true,
  "createdAt": "2025-11-17T10:30:00"
}
```

### Unsubscribe from Currency Notifications

```http
DELETE /api/subscriptions?email={email}&currencyCode={currencyCode}
```

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/subscriptions?email=user@example.com&currencyCode=USD"
```

## Available Currency Codes

The application supports all currencies provided by the NBU API, including:

- USD - US Dollar
- EUR - Euro
- GBP - British Pound
- PLN - Polish Zloty
- CAD - Canadian Dollar
- AUD - Australian Dollar
- CHF - Swiss Franc
- CNY - Chinese Yuan
- And many more...

## Scheduler

The application includes a scheduler that:

1. Runs on application startup
2. Runs daily at 9:00 AM (configurable via `scheduler.exchange-rate.cron`)
3. Fetches all exchange rates from the NBU API
4. Saves/updates rates in the database
5. Sends email notifications to all active subscribers

## Email Notifications

Subscribers receive daily email notifications with:
- Currency name and code
- Current exchange rate
- Exchange date

**Email Example:**
```
Subject: Exchange Rate Update: USD

Hello,

Here is the latest exchange rate information:

Currency: Долар США (USD)
Rate: 42.0423 UAH
Date: 2025-11-17

Best regards,
Exchange Rate Notifier
```

## Project Structure

```
src/main/java/ua/dmytrolutsiuk/backend/
├── controller/          # REST API controllers
├── dto/                 # Data Transfer Objects
├── model/               # JPA entities
├── repository/          # Spring Data repositories
├── scheduler/           # Scheduled tasks
├── service/             # Business logic
└── Application.java     # Main application class

src/test/java/ua/dmytrolutsiuk/backend/
├── controller/          # Controller tests
└── service/             # Service tests
```

## Code Quality

- Clean code architecture with separation of concerns
- Comprehensive unit tests for services and controllers
- Proper error handling and logging
- Input validation using Bean Validation
- Transaction management with `@Transactional`

## License

This project is licensed under the MIT License.
