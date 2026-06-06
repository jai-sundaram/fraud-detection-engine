# Rule-Based Transaction Fraud Detection Engine

A Spring Boot-based fraud detection engine that analyzes financial transactions in real time using multiple risk signals, including transaction velocity, geo-velocity, merchant category, transaction amount anomalies, and transaction timing. 

## Features

* Real-time transaction risk analysis
* Velocity-based fraud detection
* Geo-velocity analysis using OpenStreetMap Nominatim APIs and the Haversine formula  
* Merchant Category Code (MCC) risk scoring
* Amount anomaly detection using z-score analysis
* Time-based fraud detection
* Redis-backed transaction storage and retrieval
* Unit and integration testing with JUnit and Mockito

## Tech Stack

* Java 21
* Spring Boot
* Redis
* Docker

## Fraud Detection Rules

### Velocity Check

Evaluates the number of transactions performed within a minute.

| Transactions per Minute | Risk Score |
| ----------------------- | ---------- |
| < 3                     | 0.00       |
| 3–4                     | 5.00       |
| ≥ 5                     | 10.00      |

### Geo-Velocity Check

Calculates the implied travel speed between consecutive transactions using geolocation data and the Haversine formula.

| Implied Speed | Risk Score |
| ------------- | ---------- |
| ≤ 80 mph      | 0.00       |
| 80–300 mph    | 5.00       |
| > 300 mph     | 10.00      |

### Amount Anomaly Detection

Uses z-score analysis to identify transactions that significantly deviate from a user's historical spending behavior.
| # of Standard Deviations| Risk Score |
| ----------------------- | ---------- |
| < 3                     | 0.00       |
| 3–4                     | 5.00       |
| ≥ 5                     | 10.00      |

### Merchant Category Risk Analysis

Assigns risk scores based on Merchant Category Codes (MCCs) and whether the user has previously transacted within that category.
| Merchant                | Risk Score |
| ----------------------- | ---------- |
| Low Risk Merchant       | 1.00       |
| Medium Risk Merchant    | 5.00       |
| High Risk Merchant      | 10.00      |

If the user had no previous transactions in that category, the risk score will be multiplied by 1.5. 

### Time-Based Analysis

Flags transactions occurring during unusual hours.
| Time                    | Risk Score |
| ----------------------- | ---------- |
| 12:00AM - 5:00AM        | 1.00       |
| 5:00AM - 11:00PM        | 5.00       |

## Performance

### Redis Optimization

Average transaction processing latency:

| Configuration     | Average Latency |
| ----------------- | --------------- |
| In-Memory Storage | 143 ms          |
| Redis Storage     | 75 ms           |

**Result:** 47% reduction in average transaction processing latency.

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
GET /search/userId
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

### Run DemoApplication.java

The application will start on:

```text
http://localhost:8080
```



## Author

Jai Sundaram
