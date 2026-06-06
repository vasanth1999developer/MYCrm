# Eureka Service

## Overview

The Eureka Service is a service discovery server for the MySales microservices architecture. It acts as a registry where microservices can register themselves and discover other services in the system. This service is built using Spring Cloud Netflix Eureka Server.

## Features

- Service registration and discovery
- Health monitoring of registered services
- RESTful API for service management
- High availability support

## Technologies Used

- **Java**: 17
- **Spring Boot**: Latest stable version
- **Spring Cloud**: 2021.0.1
- **Netflix Eureka Server**: For service discovery

## Configuration

The service is configured via `application.yml`:

- **Server Port**: 8555
- **Application Name**: eureka-service
- **Eureka Settings**:
  - Does not register with Eureka (since it's the server)
  - Does not fetch registry
  - Default zone: `http://localhost:8555/eureka`

## Running the Application

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Local Development

1. Clone the repository
2. Navigate to the `eureka-service` directory
3. Run the application:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/eureka-service-0.0.1-SNAPSHOT.jar
```

The Eureka dashboard will be available at: `http://localhost:8555`

### Docker

Build and run using Docker:

```bash
docker build -t eureka-service .
docker run -p 8761:8761 eureka-service
```

## API Endpoints

- **Eureka Dashboard**: `http://localhost:8555`
- **Service Registration**: Automatic via Eureka client configuration in other services
- **Service Discovery**: Available via REST API at `/eureka/apps`

## Integration

Other microservices in the MySales system should include the Eureka client dependency and configure the Eureka server URL to register themselves:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8555/eureka
```

## CI/CD

The service includes a Jenkins pipeline for automated building and deployment. The pipeline:

1. Checks out the code from the repository
2. Builds the application using Maven
3. Runs the JAR file

## Project Structure

```
eureka-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/microservices/eurekaservice/
│   │   │       └── EurekaServiceApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── Dockerfile
├── jenkinsfile
├── pom.xml
└── README.md
```

## Contributing

1. Follow the existing code style and structure
2. Add tests for new features
3. Update documentation as needed
4. Create a pull request with a clear description of changes

## License

This project is part of the MySales microservices suite.
