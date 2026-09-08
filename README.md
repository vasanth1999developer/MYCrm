# MyCrm - CRM Backend Platform

A production-grade **CRM (Customer Relationship Management)** backend 
built with Java Spring Boot Microservices architecture.

---

##  Architecture

This project follows a **Microservices architecture** with each service 
handling a specific business domain.

```
Client → API Gateway → Eureka (Service Discovery) → Microservices → DB
```

---

##  Services

| Service | Description |
|---|---|
| `gateway-service` | API Gateway - single entry point for all requests |
| `eureka-service` | Service Discovery - tracks all running services |
| `authentication-service` | Handles login, signup, JWT token generation |
| `authorization-service` | Role-based access control, token validation |
| `customer-service` | Customer management - CRUD operations |
| `lead-service` | Sales lead tracking and management |
| `company-service` | Company profile management |
| `employee-service` | Employee records and management |
| `account-service` | Account management |
| `opportunity-service` | Sales opportunity pipeline |
| `salesorder-service` | Sales order processing |
| `inventory-service` | Product inventory tracking |
| `product-service` | Product catalog management |
| `project-service` | Project tracking |
| `TaskManagement` | Task assignment and tracking |
| `urlshortener` | Internal URL shortening utility |

---

##  Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Database | PostgreSQL |
| Caching | Redis |
| Messaging | RabbitMQ |
| Build Tool | Maven |
| Version Control | Git + GitHub |

---

##  Branch Strategy (Git Flow)

```
main        → production-ready code
develop     → integration branch
feature/*   → individual feature development
hotfix/*    → emergency production fixes
```

---

##  How to Run Locally

```bash
# Clone the repository
git clone https://github.com/vasanth1999developer/MYCrm.git

# Start Eureka service first
cd eureka-service
mvn spring-boot:run

# Then start gateway
cd gateway-service
mvn spring-boot:run

# Then start any service
cd customer-service
mvn spring-boot:run
```

---

##  Author

**Vasanth S**  
Java Backend Developer  
[GitHub](https://github.com/vasanth1999developer)
