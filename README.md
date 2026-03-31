<<<<<<< HEAD
# SmartCourier Delivery Management System (Microservices Backend)

SmartCourier is a Spring Boot + Spring Cloud microservices backend with:
- API Gateway + JWT authorization
- Eureka service discovery
- Config Server
- RabbitMQ asynchronous messaging
- Zipkin distributed tracing
- SonarQube/SonarLint static analysis configuration

## Services and Ports

- `api-gateway` -> `8080`
- `auth-service` -> `8081`
- `delivery-service` -> `8082`
- `tracking-service` -> `8083`
- `admin-service` -> `8084`
- `config-server` -> `8888`
- `eureka-server` -> `8761`

## API Coverage

Total endpoints implemented: **62**
- Auth: 14
- Delivery: 22
- Tracking: 11
- Admin: 15

## Prerequisites

- Java 17+
- Maven / Maven Wrapper
- MySQL (four schemas)
- RabbitMQ
- Zipkin

## Database Setup

Create these MySQL databases:
- `smartcourier_auth_db`
- `smartcourier_delivery_db`
- `smartcourier_tracking_db`
- `smartcourier_admin_db`

Default DB credentials used in configs:
- username: `root`
- password: `tiger`

## Infrastructure Setup

### RabbitMQ
- Broker: `localhost:5672`
- Management UI: `http://localhost:15672`
- Default user/pass: `guest / guest`

### Zipkin
- UI: `http://localhost:9411`
- Trace endpoint used by services: `http://localhost:9411/api/v2/spans`

## Run Order

Start services in this exact order:
1. `eureka-server`
2. `config-server`
3. `auth-service`
4. `delivery-service`
5. `tracking-service`
6. `admin-service`
7. `api-gateway`

Local helper script from the project root:

```powershell
.\start-smartcourier-local.ps1
```

The script uses the current workspace path automatically, checks MySQL and RabbitMQ ports, and starts Zipkin from `C:\Users\ASUS\tools\zipkin\zipkin-server.jar` when needed.

Run each service from its module folder:

```bash
./mvnw spring-boot:run
```

## API Access

Gateway base URL:

```text
http://localhost:8080/gateway
```

## RabbitMQ Flow

- `delivery-service` publishes status events
- `tracking-service` consumes them and saves tracking events

Exchange and queue:
- exchange: `smartcourier.events.exchange`
- routing key: `delivery.status.changed`
- queue: `tracking.delivery.status.queue`

## SonarQube / SonarLint

Configuration file:
- `sonar-project.properties`

Scan command from project root:

```bash
sonar-scanner
```

or with Maven per module:

```bash
./mvnw sonar:sonar
```

## Postman

Import:
- `SmartCourier.postman_collection.json`

Set variables:
- `baseUrl = http://localhost:8080`
- `token = <JWT from /gateway/auth/login>`

## Swagger

- `auth-service`: `http://localhost:8081/swagger-ui/index.html`
- `delivery-service`: `http://localhost:8082/swagger-ui/index.html`
- `tracking-service`: `http://localhost:8083/swagger-ui/index.html`
- `admin-service`: `http://localhost:8084/swagger-ui/index.html`

## Feign Integration

- `admin-service` now uses OpenFeign with Eureka discovery to fetch delivery data for the dashboard.


[//]: # (**SmartCourier Runbook (save this)**
[//]: # (
[//]: # (## A) Prerequisites (once)
[//]: # (1. Java installed (`java -version`)
[//]: # (2. MySQL running
[//]: # (3. RabbitMQ running (`http://localhost:15672`)
[//]: # (4. Zipkin jar available (`C:\Users\ASUS\tools\zipkin\zipkin-server.jar`)
[//]: # (
[//]: # (MySQL databases required:
[//]: # (- `smartcourier_auth_db`
[//]: # (- `smartcourier_delivery_db`
[//]: # (- `smartcourier_tracking_db`
[//]: # (- `smartcourier_admin_db`
[//]: # (
[//]: # (Default DB creds in project:
[//]: # (- user: `root`
[//]: # (- password: `tiger`
[//]: # (
[//]: # (---
[//]: # (
[//]: # (## B) Start infrastructure first
[//]: # (
[//]: # (### 1) Start RabbitMQ
[//]: # (If service installed:
[//]: # (```cmd
[//]: # (net start RabbitMQ
[//]: # (```
[//]: # (Verify:
[//]: # (- `http://localhost:15672` (guest/guest)
[//]: # (
[//]: # (### 2) Start Zipkin
[//]: # (Open new terminal:
[//]: # (```powershell
[//]: # (java -jar "$env:USERPROFILE\tools\zipkin\zipkin-server.jar"
[//]: # (```
[//]: # (Verify:
[//]: # (- `http://localhost:9411`
[//]: # (
[//]: # (---
[//]: # (
[//]: # (## C) Start microservices (exact order)
[//]: # (
[//]: # (Open separate terminal/IntelliJ run for each:
[//]: # (
[//]: # (1. `eureka-server`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\eureka-server"
[//]: # (mvnw.cmd spring-boot:run
[//]: # (```
[//]: # (
[//]: # (2. `config-server`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\config-server"
[//]: # (mvnw.cmd spring-boot:run
[//]: # (```
[//]: # (
[//]: # (3. `auth-service`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\auth-service"
[//]: # (mvnw.cmd spring-boot:run
[//]: # (```
[//]: # (
[//]: # (4. `delivery-service`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\delivery-service"
[//]: # (mvnw.cmd spring-boot:run
[//]: # (```
[//]: # (
[//]: # (5. `tracking-service`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\tracking-service"
[//]: # (mvnw.cmd spring-boot:run
[//]: # (```
[//]: # (
[//]: # (6. `admin-service`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\admin-service"
[//]: # (mvnw.cmd spring-boot:run
[//]: # (```
[//]: # (
[//]: # (7. `api-gateway`
[//]: # (```cmd
[//]: # (cd "C:\Users\ASUS\Documents\New project\smartcourier\api-gateway"
[//]: # (mvnw.cmd clean spring-boot:run
[//]: # (```
[//]: # (
[//]: # (---
[//]: # (
[//]: # (## D) Quick health checks
[//]: # (
[//]: # (- Eureka UI: `http://localhost:8761`
[//]: # (- Gateway health: `http://localhost:8080/actuator/health`
[//]: # (- RabbitMQ UI: `http://localhost:15672`
[//]: # (- Zipkin UI: `http://localhost:9411`
[//]: # (
[//]: # (Check service ports:
[//]: # (- 8080 gateway
[//]: # (- 8081 auth
[//]: # (- 8082 delivery
[//]: # (- 8083 tracking
[//]: # (- 8084 admin
[//]: # (- 8761 eureka
[//]: # (
[//]: # (---
[//]: # (
[//]: # (## E) Postman test flow
[//]: # (
[//]: # (1. Import collection:
[//]: # (`SmartCourier_Fresh_Fixed.postman_collection.json`
[//]: # (2. Set collection variable:
[//]: # (- `baseUrl = http://localhost:8080`
[//]: # (3. Run:
[//]: # (1. Signup Customer
[//]: # (2. Signup Admin
[//]: # (3. Login (Auto Save Token)
[//]: # (4. Validate Token
[//]: # (5. Delivery Health
[//]: # (6. Delivery Create
[//]: # (7. Tracking Health
[//]: # (8. Admin Dashboard
[//]: # (
[//]: # (---
[//]: # (
[//]: # (## F) If Zipkin connection refused logs appear
[//]: # (Either:
[//]: # (1. Ensure Zipkin is running on `9411`, or
[//]: # (2. Temporarily disable tracing by env var in each service:
[//]: # (`MANAGEMENT_TRACING_ENABLED=false`
[//]: # (
[//]: # (---
[//]: # (
[//]: # (## G) Stop everything quickly
[//]: # (```cmd
[//]: # (taskkill /F /IM java.exe
[//]: # (```
[//]: # ((and close Zipkin terminal if separate)
[//]: # (
[//]: # (---
[//]: # (
[//]: # (If you want, I can convert this runbook into a `RUN_STEPS.md` file inside your project so you always have it
ready.)
=======
SmartCourierDeliveryManagementSystem_Sprint
>>>>>>> a537484902341e89190ef6da83f485baa0c729ed
