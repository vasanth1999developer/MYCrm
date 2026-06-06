# Gateway Service

## Overview

The Gateway Service is an API Gateway that serves as the single entry point for all client requests in the MySales microservices architecture. Built using Spring Cloud Gateway, it handles request routing, authentication/authorization, and load balancing across multiple backend microservices.

## Features

- **Centralized Routing**: Routes incoming requests to appropriate microservices based on path patterns
- **Authentication & Authorization**: JWT token validation for secured endpoints
- **Load Balancing**: Automatic load balancing using Netflix Eureka
- **Service Discovery**: Dynamic discovery of microservices via Eureka
- **CORS Support**: Cross-Origin Resource Sharing configuration
- **Custom Filters**: Request/response filtering capabilities
- **Reactive Processing**: Non-blocking request handling using Spring WebFlux

## Technologies Used

- **Java**: 17
- **Spring Boot**: Latest stable version
- **Spring Cloud**: 2021.0.2
- **Spring Cloud Gateway**: API gateway framework
- **Spring Cloud Netflix Eureka Client**: Service discovery client
- **JWT (JJWT)**: JSON Web Token for authentication (v0.11.5)
- **Spring WebFlux**: Reactive framework for non-blocking I/O

## Configuration

The service is configured via `application.properties`:

- **Server Port**: 8556
- **Application Name**: api-gateway-service
- **Service Discovery**: Connected to Eureka at `http://localhost:8555/eureka`
- **Web Application Type**: Reactive (WebFlux)

## Routed Services

The gateway routes requests to the following microservices:

| Route ID | Service | Base Path | Protected |
|----------|---------|-----------|-----------|
| authendication-core | AUTHENDICATION-SERVICE | `/auth/**` | ❌ |
| inventory-core | INVENTORY-SERVICE | `/inventory/**` | ✅ |
| campaign-core | CAMPAIGN-SERVICE | `/campaign/**` | ✅ |
| employee-core | EMPLOYEE-SERVICE | `/employee/**` | ✅ |
| opportunity-core | USER-OPPORTUNITY | `/opportunity/**` | ✅ |
| customer-core | CUSTOMER-SERVICE | `/customer/**` | ✅ |
| lead-core | LEAD-SERVICE | `/lead/**` | ✅ |
| account-core | ACCOUNT-SERVICE | `/account/**` | ✅ |
| contacts-core | CONTACTS-SERVICE | `/contact/**` | ✅ |
| salesorder-core | SALESORDER-SERVICE | `/salesorder/**` | ✅ |
| task-core | TASK-SERVICE | `/task/**` | ✅ |
| project-core | PROJECT-SERVICE | `/project/**` | ✅ |
| authorization-core | AUTHORIZATION-SERVICE | `/authorization/**` | ✅ |
| company-core | COMPANY-SERVICE | `/company/**` | ✅ |

## Project Structure

```
gateway-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/microservices/apigatewayservice/
│   │   │       ├── ApiGatewayServiceApplication.java
│   │   │       ├── config/
│   │   │       │   ├── AppConfig.java
│   │   │       │   ├── CorsConfig.java
│   │   │       │   └── DncResolutionFixer.java
│   │   │       ├── filter/
│   │   │       │   ├── AuthenticationFilter.java
│   │   │       │   └── RouteValidator.java
│   │   │       └── utils/
│   │   │           └── JwtUtil.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application.yml
│   └── test/
├── jenkinsfile
├── pom.xml
└── README.md
```

## Key Components

### ApiGatewayServiceApplication
Main application class with Eureka client discovery enabled.

### AuthenticationFilter
Custom gateway filter that validates JWT tokens for protected routes. Intercepts all requests matching protected path patterns and verifies token validity.

### RouteValidator
Utility class to validate and determine if a request route requires authentication.

### CorsConfig
Configuration for Cross-Origin Resource Sharing to handle requests from different origins.

### DncResolutionFixer
DNS resolution configuration for proper service discovery.

## Running the Application

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Eureka Server running on `localhost:8555`

### Local Development

1. Clone the repository
2. Navigate to the `gateway-service` directory
3. Run the application:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/gateway-service-0.0.1-SNAPSHOT.jar
```

The gateway will be available at: `http://localhost:8556`

## Authentication

Protected routes require a valid JWT token in the Authorization header:

```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8556/inventory/products
```

### Token Generation

Obtain JWT tokens from the Authentication Service:

```bash
curl -X POST http://localhost:8556/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "password"}'
```

## API Gateway Endpoints

- **Authentication**: `POST /auth/login` - Generate JWT token
- **Inventory**: `GET/POST /inventory/**` - Inventory service endpoints
- **Customers**: `GET/POST /customer/**` - Customer service endpoints
- **Leads**: `GET/POST /lead/**` - Lead service endpoints
- **Accounts**: `GET/POST /account/**` - Account service endpoints
- **Employees**: `GET/POST /employee/**` - Employee service endpoints
- **Products**: `GET/POST /inventory/**` - Product service endpoints
- **Opportunities**: `GET/POST /opportunity/**` - Opportunity service endpoints
- **Sales Orders**: `GET/POST /salesorder/**` - Sales order service endpoints
- **Tasks**: `GET/POST /task/**` - Task management endpoints
- **Projects**: `GET/POST /project/**` - Project service endpoints
- **Authorization**: `GET/POST /authorization/**` - Authorization service endpoints
- **Company**: `GET/POST /company/**` - Company service endpoints

## Filter Chain

All protected routes pass through the `AuthenticationFilter` which:

1. Extracts the JWT token from the Authorization header
2. Validates the token using `JwtUtil`
3. Sets authenticated user information in request context
4. Forwards request to backend service or returns 401 Unauthorized

## Load Balancing

The gateway uses Spring Cloud LoadBalancer with the `lb://` scheme to distribute requests across multiple instances of backend services registered with Eureka.

## CI/CD

The service includes a Jenkins pipeline for automated building and deployment. The pipeline:

1. Checks out the code from the repository
2. Builds the application using Maven
3. Runs the JAR file

## Service Discovery Integration

The gateway automatically discovers all registered services from Eureka. When a microservice registers or deregisters, the gateway updates its routing table dynamically.

## Troubleshooting

### Gateway Not Finding Services
- Ensure Eureka Server is running on `localhost:8555`
- Verify backend services are registered with Eureka
- Check service names match the configuration

### Authentication Failures
- Verify JWT token is valid and not expired
- Check token is sent in Authorization header with "Bearer" prefix
- Ensure secret key matches between gateway and authentication service

### CORS Errors
- Check `CorsConfig` configuration matches your client domain
- Verify CORS preflight requests are not being blocked

## Contributing

1. Follow the existing code style and structure
2. Add tests for new features
3. Update this README if adding new routes
4. Create a pull request with a clear description of changes

## License

This project is part of the MySales microservices suite.

