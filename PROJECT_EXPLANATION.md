# SmartCourier Project Explanation

## 1. Project Summary

SmartCourier is a **Spring Boot microservices backend** for delivery management. It is split into multiple independent services that communicate using:

- HTTP APIs
- Eureka service discovery
- Config Server centralized configuration
- API Gateway routing and JWT filtering
- RabbitMQ asynchronous messaging
- Feign Client synchronous service-to-service communication
- Zipkin distributed tracing

The project demonstrates both **synchronous communication** and **event-driven communication**.

## 2. Services Used In The Project

### 1. Eureka Server

Purpose:
- service registry
- keeps track of all running microservices

Why it is used:
- services do not need hardcoded URLs for one another
- services register themselves
- other services can discover them by service name

In this project:
- [EurekaServerApplication.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\eureka-server\src\main\java\com\smartcourier\eureka\EurekaServerApplication.java)
- runs on port `8761`

Internal working:
- each client service sends registration and heartbeat requests to Eureka
- Eureka stores service name, host, and port
- if a service goes down and stops heartbeats, Eureka can mark it unavailable

### 2. Config Server

Purpose:
- centralized configuration management

Why it is used:
- configuration for client services is served from one place
- easier to manage distributed systems

In this project:
- [ConfigServerApplication.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\config-server\src\main\java\com\smartcourier\config\ConfigServerApplication.java)
- configuration files are under [config folder](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\config-server\src\main\resources\config)

Internal working:
- client service starts
- reads `spring.config.import`
- calls Config Server
- Config Server returns that service’s config by service name

### 3. API Gateway

Purpose:
- single entry point for clients

Why it is used:
- clients do not call every microservice separately
- central place for routing and JWT validation

In this project:
- [ApiGatewayApplication.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\api-gateway\src\main\java\com\smartcourier\gateway\ApiGatewayApplication.java)
- routes configured in [application.yaml](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\api-gateway\src\main\resources\application.yaml)
- JWT filter in [JwtFilter.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\api-gateway\src\main\java\com\smartcourier\gateway\filter\JwtFilter.java)

Internal working:
- request comes to gateway
- gateway checks JWT
- gateway checks role authorization
- gateway forwards request to target service using Eureka service ID

### 4. Auth Service

Purpose:
- signup
- login
- user management
- JWT token generation and validation support

Main responsibility:
- authentication and user identity

Key files:
- [AuthController.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\auth-service\src\main\java\com\smartcourier\auth\controller\AuthController.java)
- [AuthServiceImpl.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\auth-service\src\main\java\com\smartcourier\auth\service\impl\AuthServiceImpl.java)
- [JwtUtil.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\auth-service\src\main\java\com\smartcourier\auth\security\JwtUtil.java)

### 5. Delivery Service

Purpose:
- manage delivery creation and delivery lifecycle

Main responsibility:
- create delivery
- assign agent
- update status
- publish status change events

Key files:
- [DeliveryController.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\delivery-service\src\main\java\com\smartcourier\delivery\controller\DeliveryController.java)
- [DeliveryServiceImpl.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\delivery-service\src\main\java\com\smartcourier\delivery\service\impl\DeliveryServiceImpl.java)
- [DeliveryEventPublisher.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\delivery-service\src\main\java\com\smartcourier\delivery\messaging\DeliveryEventPublisher.java)

### 6. Tracking Service

Purpose:
- maintain delivery tracking events

Main responsibility:
- consume delivery status events from RabbitMQ
- store tracking history
- expose tracking APIs

Key files:
- [TrackingController.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\tracking-service\src\main\java\com\smartcourier\tracking\controller\TrackingController.java)
- [TrackingServiceImpl.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\tracking-service\src\main\java\com\smartcourier\tracking\service\impl\TrackingServiceImpl.java)
- [DeliveryStatusEventListener.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\tracking-service\src\main\java\com\smartcourier\tracking\messaging\DeliveryStatusEventListener.java)

### 7. Admin Service

Purpose:
- reports
- dashboard
- analytics

Main responsibility:
- fetch delivery data using Feign Client
- generate report data

Key files:
- [AdminController.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\admin-service\src\main\java\com\smartcourier\admin\controller\AdminController.java)
- [AdminServiceImpl.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\admin-service\src\main\java\com\smartcourier\admin\service\impl\AdminServiceImpl.java)
- [DeliveryClient.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\admin-service\src\main\java\com\smartcourier\admin\client\DeliveryClient.java)

## 3. Project Workflow

## Login Workflow

1. User calls auth login API
2. Auth service verifies username and password
3. Auth service generates JWT
4. Client uses JWT for future requests

## Gateway Workflow

1. Client sends request to gateway
2. Gateway extracts token from `Authorization` header
3. Gateway validates token
4. Gateway extracts username and role
5. Gateway adds `X-User` and `X-Role` headers
6. Gateway forwards request to target microservice

## Delivery Workflow

1. Customer logs in
2. Customer creates delivery
3. Delivery service stores delivery in database
4. Delivery service generates tracking number
5. Delivery service publishes event to RabbitMQ

## Tracking Workflow

1. RabbitMQ receives delivery status event
2. Tracking service consumes event using listener
3. Tracking event is stored in tracking database
4. Client can fetch tracking history using tracking APIs

## Admin Dashboard Workflow

1. Admin calls dashboard API
2. Admin service uses Feign Client to call delivery-service
3. Delivery-service returns delivery list
4. Admin service calculates total, shipped, delivered, and cancelled counts
5. Dashboard response is returned

## Zipkin Workflow

1. Request enters gateway or service
2. trace ID and span IDs are generated
3. spans are created for each service call
4. spans are reported to Zipkin
5. Zipkin UI shows request path through services

## 4. What Is Feign Client

Feign Client is a **declarative HTTP client** in Spring Cloud.

Meaning:
- instead of writing `RestTemplate` or `WebClient` code manually
- we define a Java interface
- Spring creates the HTTP client implementation automatically

In this project:
- [DeliveryClient.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\admin-service\src\main\java\com\smartcourier\admin\client\DeliveryClient.java)

```java
@FeignClient(name = "delivery-service")
public interface DeliveryClient {
    @GetMapping("/deliveries")
    List<DeliverySummaryDTO> getAllDeliveries();
}
```

Why Feign is used here:
- admin-service needs delivery data
- delivery-service already exposes that API
- Feign makes service-to-service communication simple and clean
- Eureka resolves `delivery-service` automatically

Why not direct database access:
- in microservices, each service owns its own database
- one service should not directly access another service’s database
- communication should happen via APIs or messaging

## 5. What Is RabbitMQ

RabbitMQ is a **message broker**.

Purpose:
- asynchronous communication between services

In this project:
- delivery-service publishes delivery status events
- tracking-service consumes those events

Why RabbitMQ is used:
- delivery-service and tracking-service stay loosely coupled
- delivery-service does not need to wait for tracking-service response
- event-driven design improves scalability

Important concepts:

### Producer
- service that sends message
- here: `delivery-service`

### Consumer
- service that receives message
- here: `tracking-service`

### Exchange
- routes messages to queues
- here: `smartcourier.events.exchange`

### Routing Key
- rule used by exchange to decide route
- here: `delivery.status.changed`

### Queue
- stores messages until consumer receives them
- here: `tracking.delivery.status.queue`

Internal working:
- delivery created or status updated
- publisher sends `DeliveryStatusEvent`
- exchange routes event to queue
- tracking listener consumes it
- tracking event stored in DB

## 6. What Is Zipkin

Zipkin is a **distributed tracing system**.

Purpose:
- track requests across multiple services

Why it is important:
- in microservices one request may pass through many services
- debugging becomes difficult
- Zipkin shows the complete path and timings

Important terms:

### Trace
- one full request journey

### Span
- one unit of work inside a trace

### Trace ID
- unique id for entire request chain

### Span ID
- unique id for one service operation

In this project:
- gateway and services send trace data to Zipkin
- traces help observe gateway, config, eureka, and business APIs

## 7. Important Annotations Used

## Spring Boot and Service Bootstrapping

### `@SpringBootApplication`
Where:
- all main application classes

Why:
- marks the main Spring Boot app
- combines component scanning, auto configuration, and configuration support

### `@EnableEurekaServer`
Where:
- [EurekaServerApplication.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\eureka-server\src\main\java\com\smartcourier\eureka\EurekaServerApplication.java)

Why:
- turns the application into Eureka Server

### `@EnableConfigServer`
Where:
- [ConfigServerApplication.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\config-server\src\main\java\com\smartcourier\config\ConfigServerApplication.java)

Why:
- turns the application into Config Server

### `@EnableFeignClients`
Where:
- [AdminServiceApplication.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\admin-service\src\main\java\com\smartcourier\admin\AdminServiceApplication.java)

Why:
- enables Feign interfaces in admin-service

## Web Layer

### `@RestController`
Where:
- all controller classes

Why:
- marks class as REST API controller
- returns JSON/text directly

### `@RequestMapping`
Why:
- defines base path for controller

### `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
Why:
- map specific HTTP methods to Java methods

### `@RequestBody`
Why:
- binds request JSON body to DTO

### `@PathVariable`
Why:
- binds URL path value to method parameter

### `@RequestParam`
Why:
- binds query parameter to method parameter

### `@RequestHeader`
Why:
- reads headers like `Authorization` or `X-User`

### `@Valid`
Why:
- triggers bean validation for request DTOs

## Service and Component Layer

### `@Service`
Why:
- marks business logic classes

### `@Component`
Where:
- gateway JWT filter
- message listener helpers

Why:
- makes class a Spring-managed bean

### `@Configuration`
Why:
- defines configuration classes

### `@Bean`
Why:
- manually creates Spring beans like queues, exchanges, converters, security filter chain

## Persistence Layer

### `@Entity`
Why:
- marks class as JPA entity for database table mapping

### `@Table`
Why:
- customizes table name
- example: auth `User` mapped to `users`

### `@Id`
Why:
- primary key field

### `@GeneratedValue`
Why:
- auto-generates primary key

### `@Column`
Why:
- column constraints like `nullable=false`, `unique=true`

## Exception Handling

### `@RestControllerAdvice`
Where:
- all global exception handlers

Why:
- central exception handling across controller methods

### `@ExceptionHandler`
Why:
- maps specific exception types to HTTP responses

## Messaging

### `@RabbitListener`
Where:
- [DeliveryStatusEventListener.java](D:\CAPGEMENI_JAVA_FULLSTACK\Advanced_JAVA\Final_Sprint\New project\smartcourier\tracking-service\src\main\java\com\smartcourier\tracking\messaging\DeliveryStatusEventListener.java)

Why:
- listens to RabbitMQ queue and consumes events automatically

## Testing

### `@SpringBootTest`
Why:
- loads Spring application context for integration-style tests

### `@ActiveProfiles("test")`
Why:
- uses test configuration instead of real infrastructure

### `@ExtendWith(MockitoExtension.class)`
Why:
- enables Mockito in unit tests

### `@Mock`
Why:
- creates mock dependency

### `@InjectMocks`
Why:
- injects mocks into class under test

## 8. Security Concept Used

This project uses **JWT-based authentication**.

Flow:
- Auth service creates token after login
- Gateway validates token
- Gateway checks role-based access
- Gateway forwards user identity headers to downstream services

This means:
- services trust gateway-forwarded user context
- gateway acts as a security enforcement layer

## 9. Validation and Error Handling

Validation:
- DTOs are validated using `@Valid`
- if input is invalid, `MethodArgumentNotValidException` is handled globally

Error handling:
- custom exceptions like `UserNotFoundException`, `DeliveryNotFoundException`, `TrackingNotFoundException`, `ResourceNotFoundException`
- converted into proper HTTP responses by global exception handlers

## 10. Swagger

Swagger is used for API documentation and testing.

Why it is important:
- easy endpoint exploration
- useful during demo and evaluation
- shows request and response models

## 11. Databases Used

Each business service has its own separate MySQL database:

- auth-service -> `smartcourier_auth_db`
- delivery-service -> `smartcourier_delivery_db`
- tracking-service -> `smartcourier_tracking_db`
- admin-service -> `smartcourier_admin_db`

This follows microservice database-per-service design.

## 12. Internal Working Summary

### If user creates delivery
- gateway authenticates request
- delivery-service stores delivery
- event published to RabbitMQ
- tracking-service consumes event
- tracking history becomes available
- Zipkin captures trace

### If admin opens dashboard
- gateway checks admin token
- admin-service calls delivery-service using Feign
- admin-service aggregates counts
- response returned
- Zipkin shows service interaction

## 13. Important Concepts Used In The Project

- Microservices architecture
- Service discovery
- Centralized configuration
- API Gateway pattern
- JWT authentication
- Role-based authorization
- Declarative REST client using Feign
- Event-driven communication using RabbitMQ
- Distributed tracing using Zipkin
- Global exception handling
- Validation using Bean Validation
- Database-per-service design
- Swagger API documentation
- Actuator-based observability

## 14. Evaluation-Friendly One-Line Summary

SmartCourier is a Spring Boot microservices-based courier backend where clients access services through an API Gateway, services are discovered using Eureka, configuration is centralized using Config Server, admin-service uses Feign for synchronous inter-service communication, delivery-service and tracking-service communicate asynchronously using RabbitMQ, and Zipkin provides distributed tracing across the system.
