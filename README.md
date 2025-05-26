# 🌆 CityInfo API

A Spring Boot application that aggregates city-specific information from multiple public APIs (OpenWeatherMap, REST Countries, and News API) and provides a consolidated REST API endpoint for clients. Data can be persisted in a PostgreSQL database and optionally exported in CSV or JSON formats.

---

## ✨ Features

* ✅ Fetch **temperature**, **country details**, **language**, **bordering countries**, and **top news headlines** for a list of cities.
* 📄 Optional data export (CSV / JSON).
* 🧠 Configurable historical data storage and automatic updates.
* 📀 Stores results in a PostgreSQL database.
* 🌐 Consumes:

  * [OpenWeatherMap](https://openweathermap.org/)
  * [REST Countries](https://restcountries.com/)
  * [News API](https://newsapi.org/)
* 🨲 Comprehensive unit tests using JUnit and Mockito.
* 🐳 Dockerized for easy deployment.
* 📘 Swagger UI for interactive API documentation.

---

## 📦 Tech Stack

* **Java 17**
* **Spring Boot 3.0.5**
* **Spring WebFlux**
* **Spring Data JPA**
* **PostgreSQL**
* **Lombok**
* **OpenCSV**
* **JUnit 5 + Mockito**
* **springdoc-openapi (Swagger 3)**

---

## 📁 Project Structure

```plaintext
src
🕼️ main
🕼️🕼️ java/com/example/cityinfo
🕼️🕼️🕼️ config/                  # API keys, WebClient config
🕼️🕼️🕼️ controller/              # CityInfoController
🕼️🕼️🕼️ dto/                     # Data Transfer Objects
🕼️🕼️🕼️ entity/                  # CityInfo JPA Entity
🕼️🕼️🕼️ exception/               # GlobalExceptionHandler
🕼️🕼️🕼️ repository/              # JPA repository
🕼️🕼️🕼️ service/                 # Business logic
🕼️🕼️🕼️ CityInfoApiApplication   # Spring Boot main class
🕼️🕼️ resources/
🕼️🕼️🕼️ application.properties   # App config
🕼️ test/java/com/example/cityinfo/
    🕼️ ...                          # Unit tests
```

---

## ⚙️ Configuration

In `application.properties`:

```properties
api.openweathermap.key=YOUR_OPENWEATHERMAP_API_KEY
api.newsapi.key=YOUR_NEWSAPI_KEY
spring.datasource.url=jdbc:postgresql://localhost:5432/cityinfo
spring.datasource.username=youruser
spring.datasource.password=yourpassword
cityinfo.update-interval-hours=24
```

---

## 🚀 Running the Application

To build, test, and run the application using the provided PowerShell script:

```powershell
./start.ps1
```

This script will:

* Clean and install the Maven project
* Run the test suite
* Start the Spring Boot application

Make sure PostgreSQL is running and accessible with the provided credentials.

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 📘 API Documentation (Swagger)

Once the app is running, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

This interactive UI allows you to:

* Explore and test endpoints
* View input parameters and output schema
* Generate documentation automatically from code annotations

API documentation is powered by `springdoc-openapi-starter-webflux-ui`.

---

## 🐳 Docker

### 🔧 Build and Run with Docker CLI

```bash
# Build image
docker build -t cityinfo-api .

# Run container with environment variables
docker run -p 8080:8080 \
  -e API_OPENWEATHERMAP_KEY=your_key \
  -e API_NEWSAPI_KEY=your_key \
  cityinfo-api
```

> ⚠️ Make sure your `application.properties` reads from these env variables using `@Value`.

---

## 🐳 Docker Compose

### 📦 `docker-compose.yml`

```yaml
version: '3.8'

services:
  db:
    image: postgres:14
    restart: always
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: cityinfo
      POSTGRES_USER: cityuser
      POSTGRES_PASSWORD: citypass

  app:
    build: .
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/cityinfo
      SPRING_DATASOURCE_USERNAME: cityuser
      SPRING_DATASOURCE_PASSWORD: citypass
      API_OPENWEATHERMAP_KEY: your_openweathermap_key
      API_NEWSAPI_KEY: your_newsapi_key
```

### ▶️ Run Everything

```bash
docker-compose up --build
```

Then access:

* App: [http://localhost:8080](http://localhost:8080)
* Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

To stop everything:

```bash
docker-compose down
```

---

## 💠 API Endpoint

### `GET /api/v1/cities`

**Query Parameters:**

* `cities`: list of cities (required)
* `exportFormat`: `JSON` (default) or `CSV`
* `includeResponseBody`: `true`/`false`
* `saveToDb`: `true`/`false`

**Example:**

```http
GET /api/v1/cities?cities=Berlin,Paris&exportFormat=JSON&includeResponseBody=true
```

**Response:**

```json
[
  {
    "city": "Berlin",
    "temperature": 12.0,
    "countryCode": "DE",
    "language": "German",
    "borderingCountries": ["FR", "PL", "CZ"],
    "topNewsHeadlines": ["News headline 1", "News headline 2"]
  }
]
```

---

## 🧹 API Sources

| Source         | Base URL                               | Auth Required | Description               |
| -------------- | -------------------------------------- | ------------- | ------------------------- |
| OpenWeatherMap | `https://api.openweathermap.org`       | ✅             | Weather by city           |
| REST Countries | `https://restcountries.com/v3.1`       | ❌             | Country metadata          |
| News API       | `https://newsapi.org/v2/top-headlines` | ✅             | News headlines by country |

---

## 📓 Author

Developed as part of the **API Developer Assessment** for **John Galt**
👨‍💻 by **Vitalii Yuzvyk**
