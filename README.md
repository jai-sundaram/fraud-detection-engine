# Fraud Detection Engine

A Spring Boot-based fraud detection engine that analyzes financial transactions in real time using multiple risk signals, including transaction velocity, geo-velocity, merchant category, transaction amount anomalies, and transaction timing. The system leverages Redis for efficient transaction storage and retrieval, reducing average transaction processing latency by 47%.

## Features

* Real-time transaction risk analysis
* Velocity-based fraud detection
* Geo-velocity analysis using OpenStreetMap Nominatim APIs
* Merchant Category Code (MCC) risk scoring
* Amount anomaly detection using z-score analysis
* Time-based fraud detection
* Redis-backed transaction storage and retrieval
* REST API for transaction processing
* Unit and integration testing with JUnit and Mockito

## Tech Stack

* Java 21
* Spring Boot
* Redis
* Docker
* JUnit 5
* Mockito
* OpenStreetMap Nominatim API

## Fraud Detection Rules

### Velocity Check

Evaluates the number of transactions performed within a short time window.

| Transactions per Minute | Risk Score |
| ----------------------- | ---------- |
| < 3                     | Low        |
| 3–4                     | Medium     |
| ≥ 5                     | High       |

### Geo-Velocity Check

Calculates the implied travel speed between consecutive transactions using geolocation data and the Haversine formula.

| Implied Speed | Risk Score |
| ------------- | ---------- |
| ≤ 80 mph      | Low        |
| 80–300 mph    | Medium     |
| > 300 mph     | High       |

### Amount Anomaly Detection

Uses z-score analysis to identify transactions that significantly deviate from a user's historical spending behavior.

### Merchant Category Risk Analysis

Assigns risk scores based on Merchant Category Codes (MCCs) and whether the user has previously transacted within that category.

### Time-Based Analysis

Flags transactions occurring during unusual hours (12 AM – 5 AM).

## Performance

### Redis Optimization

Average transaction processing latency:

| Configuration     | Average Latency |
| ----------------- | --------------- |
| In-Memory Storage | 143 ms          |
| Redis Storage     | 75 ms           |

**Result:** 47% reduction in average transaction processing latency.

## Architecture

```text
Client Request
      |
      v
Transaction API
      |
      v
Fraud Detection Engine
      |
      +--> Velocity Analysis
      |
      +--> Geo-Velocity Analysis
      |
      +--> Amount Anomaly Detection
      |
      +--> Merchant Risk Analysis
      |
      +--> Time-Based Analysis
      |
      v
Risk Score Calculation
      |
      v
Transaction Result
```

## API Endpoints

### Store Transaction

```http
POST /store
```

Request Body:

```json
{
  "userId": "1",
  "amount": 120.50,
  "location": "Chicago, Illinois",
  "timestamp": "2026-05-22T10:15:00",
  "merchantCategory": 5411
}
```

### Retrieve User Transactions

```http
GET /getAll?userId=1
```

## Running the Project

### Clone Repository

```bash
git clone <repository-url>
cd fraud-detection-engine
```

### Start Redis

```bash
docker compose up -d
```

### Run Application

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

## Testing

Run unit tests:

```bash
mvn test
```

## Future Improvements

* PostgreSQL persistence layer
* JWT authentication and authorization
* Machine learning-based fraud scoring
* Kafka event streaming integration
* Azure deployment pipeline
* Real-time monitoring dashboard

## Author

Jai Sundaram
