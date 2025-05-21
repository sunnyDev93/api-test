# 🌆 CityInfo API

A Spring Boot application that aggregates city-specific information from multiple public APIs (OpenWeatherMap, REST Countries, and News API) and provides a consolidated REST API endpoint for clients. Data can be persisted in a PostgreSQL database and optionally exported in CSV or JSON formats.

---

## ✨ Features

* ✅ Fetch **temperature**, **country details**, **language**, **bordering countries**, and **top news headlines** for a list of cities.
* 📤 Optional data export (CSV / JSON).
* 🧠 Configurable historical data storage and automatic updates.
* 📀 Stores results in a PostgreSQL database.
* 🌐 Consumes:

  * [OpenWeatherMap](https://openweathermap.org/)
  * [REST Countries](https://restcountries.com/)
  * [News API](https://newsapi.org/)
* 🧲 Comprehensive unit tests using JUnit and Mockito.
* 🐳 Dockerized for easy deployment.

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

---

## 📁 Project Structure

```plaintext
src
├── main
│   ├── java/com/example/cityinfo
│   │   ├── config/                  # API keys, WebClient config
│   │   ├── controller/              # CityInfoController
│   │   ├── dto/                     # Data Transfer Objects
│   │   ├── entity/                  # CityInfo JPA Entity
│   │   ├── exception/               # GlobalExceptionHandler
│   │   ├── repository/              # JPA repository
│   │   ├── service/                 # Business logic
│   │   └── CityInfoApiApplication   # Spring Boot main class
│   └── resources/
│       └── application.properties   # App config
└── test/java/com/example/cityinfo/
    └── ...                          # Unit tests
```

---

## ⚙️ Configuration

In `application.properties`:

```properties
weather.api.key=YOUR_OPENWEATHER_API_KEY
news.api.key=YOUR_NEWSAPI_KEY
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

## 🥪 Running Tests

> ⚠️ Normally, tests are run as part of `start.ps1`. To run them manually:
> bash
> mvn test

````

---

## 🐳 Docker

To build and run the application using Docker:

```bash
docker build -t cityinfo-api .
docker run -p 8080:8080 --env-file .env cityinfo-api
````

---

## 🛠️ API Endpoint

### `POST /api/cityinfo`

**Request Body Example:**

```json
{
  "cities": ["Berlin", "Paris", "New York"],
  "export": true,
  "includeResponse": true
}
```

**Response:**

```json
[
  {
    "city": "Berlin",
    "temperature": "12°C",
    "countryCode": "DE",
    "language": "German",
    "borders": ["FR", "PL", "CZ"],
    "topHeadlines": ["News headline 1", "News headline 2"]
  },
  ...
]
```

---

## 📤 Export Options

* Exported CSV and JSON files will be generated in the configured output directory or returned in the API response if `includeResponse=true`.

---

## 🧹 API References

| Source         | Base URL                               | Auth Required | Description               |
| -------------- | -------------------------------------- | ------------- | ------------------------- |
| OpenWeatherMap | `https://api.openweathermap.org`       | ✅             | Weather by city           |
| REST Countries | `https://restcountries.com/v3.1`       | ❌             | Country metadata          |
| News API       | `https://newsapi.org/v2/top-headlines` | ✅             | News headlines by country |

---

## 📓 Author

Developed as part of the **API Developer Assessment** for **John Galt** by Vitalii Yuzvyk.

---
