# SmartCourier Run Steps

## Project Path

`D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier`

## Prerequisites

- Java 17 or above
- MySQL running on port `3306`
- RabbitMQ running on port `5672`
- Zipkin running on port `9411`

## Required Databases

Make sure these MySQL databases exist, or let the services create them:

- `smartcourier_auth_db`
- `smartcourier_delivery_db`
- `smartcourier_tracking_db`
- `smartcourier_admin_db`

Default database credentials used by the project:

- Username: `root`
- Password: `tiger`

## Infrastructure Checks

Before starting the project, verify:

- MySQL is reachable on `localhost:3306`
- RabbitMQ is reachable on `localhost:5672`
- Zipkin UI is available at `http://localhost:9411`
- RabbitMQ UI is available at `http://localhost:15672`

## Recommended Start Method

Open PowerShell in the project root and run:

```powershell
.\start-smartcourier-local.ps1
```

This starts the services in the correct order and first clears stale SmartCourier Java processes so ports like `8081` do not remain blocked from an earlier run.

If PowerShell blocks script execution on your system, use:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-smartcourier-local.ps1
```

## Manual Start Order

If you want to start each service manually, use this exact order.

### 1. Eureka Server

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\eureka-server"
.\mvnw.cmd spring-boot:run
```

### 2. Config Server

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\config-server"
.\mvnw.cmd spring-boot:run
```

### 3. Auth Service

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\auth-service"
.\mvnw.cmd spring-boot:run
```

### 4. Delivery Service

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\delivery-service"
.\mvnw.cmd spring-boot:run
```

### 5. Tracking Service

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\tracking-service"
.\mvnw.cmd spring-boot:run
```

### 6. Admin Service

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\admin-service"
.\mvnw.cmd spring-boot:run
```

### 7. API Gateway

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\api-gateway"
.\mvnw.cmd spring-boot:run
```

## Service URLs

- Eureka Server: `http://localhost:8761`
- Config Server: `http://localhost:8888`
- API Gateway: `http://localhost:8080`
- Auth Service: `http://localhost:8081`
- Delivery Service: `http://localhost:8082`
- Tracking Service: `http://localhost:8083`
- Admin Service: `http://localhost:8084`
- Zipkin: `http://localhost:9411`
- RabbitMQ UI: `http://localhost:15672`

## Quick Health Checks

After startup, verify these URLs:

- `http://localhost:8761`
- `http://localhost:8888/auth-service/default`
- `http://localhost:8080/actuator/health`
- `http://localhost:8082/deliveries/health`
- `http://localhost:8083/tracking/health`
- `http://localhost:8084/admin/health`

## Swagger URLs

- Auth Service: `http://localhost:8081/swagger-ui/index.html`
- Delivery Service: `http://localhost:8082/swagger-ui/index.html`
- Tracking Service: `http://localhost:8083/swagger-ui/index.html`
- Admin Service: `http://localhost:8084/swagger-ui/index.html`

## Gateway Base Path

Use this base path for gateway access:

`http://localhost:8080/gateway`

Examples:

- `http://localhost:8080/gateway/auth/login`
- `http://localhost:8080/gateway/deliveries`
- `http://localhost:8080/gateway/tracking`
- `http://localhost:8080/gateway/admin/dashboard`

## Postman Steps

Import this collection:

- `SmartCourier_Fresh_Fixed.postman_collection.json`

Set these values in Postman:

- `baseUrl = http://localhost:8080`
- `token = <JWT token from login>`

Suggested order:

1. Signup user or admin
2. Login
3. Copy/save JWT token
4. Test `/gateway/auth/validate`
5. Create a delivery
6. Update delivery status
7. Check tracking endpoints
8. Open admin dashboard endpoint

## RabbitMQ Flow

- `delivery-service` publishes delivery status events
- `tracking-service` consumes those events from RabbitMQ

Messaging details:

- Exchange: `smartcourier.events.exchange`
- Routing key: `delivery.status.changed`
- Queue: `tracking.delivery.status.queue`

## Feign Client

- `admin-service` uses Feign Client to call `delivery-service`
- Service discovery happens through Eureka

## Zipkin

Zipkin trace endpoint used by the services:

- `http://localhost:9411/api/v2/spans`

Open the Zipkin UI here:

- `http://localhost:9411`

## Run Tests

You can run tests module by module:

```powershell
cd "D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\auth-service"
.\mvnw.cmd test
```

Repeat similarly for:

- `delivery-service`
- `tracking-service`
- `admin-service`
- `api-gateway`
- `config-server`
- `eureka-server`

## Compile Check

To verify a service compiles:

```powershell
.\mvnw.cmd -DskipTests compile
```

## Stop All Services

From project root:

```powershell
.\stop-smartcourier-local.ps1
```

If PowerShell blocks script execution on your system, use:

```powershell
powershell -ExecutionPolicy Bypass -File .\stop-smartcourier-local.ps1
```

## If You See "Port Already In Use"

This usually means an earlier SmartCourier service is still running in the background.

Use:

```powershell
.\stop-smartcourier-local.ps1
```

Then start again with:

```powershell
.\start-smartcourier-local.ps1
```

## Notes

- Start Eureka before Config Server.
- Start Config Server before all client services.
- Start API Gateway after all business services are up.
- If any service fails, check whether MySQL, RabbitMQ, Zipkin, Eureka, and Config Server are already running.
