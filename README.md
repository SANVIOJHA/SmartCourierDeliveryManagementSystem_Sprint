# SmartCourier Delivery Management System (Microservices Backend)

SmartCourier is a **Spring Boot + Spring Cloud microservices backend** built with modern enterprise architecture.

## Features

- API Gateway with JWT Authentication & Authorization  
- Eureka Service Discovery  
- Centralized Config Server  
- RabbitMQ for Asynchronous Messaging  
- Zipkin for Distributed Tracing  
- SonarQube / SonarLint for Code Quality  

---

## Microservices & Ports

| Service            | Port  |
|-------------------|-------|
| API Gateway       | 8080  |
| Auth Service      | 8081  |
| Delivery Service  | 8082  |
| Tracking Service  | 8083  |
| Admin Service     | 8084  |
| Config Server     | 8888  |
| Eureka Server     | 8761  |

---

## API Coverage

Total Endpoints: **62**

- Auth Service → 14  
- Delivery Service → 22  
- Tracking Service → 11  
- Admin Service → 15  

---

##  Prerequisites

- Java 17+
- Maven / Maven Wrapper
- MySQL
- RabbitMQ
- Zipkin

---

##  Database Setup

Create the following MySQL databases:

- `smartcourier_auth_db`
- `smartcourier_delivery_db`
- `smartcourier_tracking_db`
- `smartcourier_admin_db`

Default credentials:

```

username: root
password: tiger

````

---

##  Infrastructure Setup

### RabbitMQ
- Broker: `localhost:5672`
- Management UI: http://localhost:15672  
- Default Credentials: `guest / guest`

### Zipkin
- UI: http://localhost:9411  
- Trace Endpoint: `http://localhost:9411/api/v2/spans`

---

##  Run Order

Start services in this sequence:

1. Eureka Server  
2. Config Server  
3. Auth Service  
4. Delivery Service  
5. Tracking Service  
6. Admin Service  
7. API Gateway  

---

##  Run Commands

From each service directory:

```bash
./mvnw spring-boot:run
````

---

##  API Access

Base URL:

```
http://localhost:8080/gateway
```

---

##  RabbitMQ Flow

* `delivery-service` publishes delivery status events
* `tracking-service` consumes and stores tracking updates

Configuration:

* Exchange: `smartcourier.events.exchange`
* Routing Key: `delivery.status.changed`
* Queue: `tracking.delivery.status.queue`

---

##  SonarQube

Run analysis:

```bash
sonar-scanner
```

or

```bash
./mvnw sonar:sonar
```

---

## Postman

Import collection:

```
SmartCourier.postman_collection.json
```

Set variables:

```
baseUrl = http://localhost:8080
token = <JWT from login>
```

---

##  Swagger URLs

* Auth Service → [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
* Delivery Service → [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
* Tracking Service → [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)
* Admin Service → [http://localhost:8084/swagger-ui/index.html](http://localhost:8084/swagger-ui/index.html)

---

##  Feign Integration

* Admin Service uses **OpenFeign with Eureka Discovery**
* Fetches delivery data for dashboard aggregation

---

##  Notes

* Ensure all infrastructure services are running before starting microservices
* Use API Gateway for all external requests
* JWT is required for secured endpoints

---

##  Author

**Sanvi Ojha**

---

## Project Status

✔ Fully Functional Microservices Backend
✔ Production-ready architecture
✔ Includes async communication & tracing

````

---
