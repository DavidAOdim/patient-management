# Patient Management System

A cloud-native, microservices-based patient management platform built with Spring Boot, gRPC, Kafka, and AWS infrastructure-as-code. The system handles patient records, authentication, billing, and analytics through independently deployable services communicating via REST, gRPC, and event streaming.

## Architecture

The platform follows a microservices architecture with an API Gateway as the single entry point, internal gRPC communication between services, and Kafka-based event streaming for asynchronous workflows (e.g., patient creation triggering billing and analytics).

```
Client
  │
  ▼
API Gateway  ──►  Auth Service (JWT validation)
  │
  ├──► Patient Service ──(gRPC)──► Billing Service
  │         │
  │         └──(Kafka/MSK)──► Analytics Service
  │
  └──► Billing Service
```

## Services

| Service | Description |
|---|---|
| **api-gateway** | Single entry point for all client requests. Routes traffic to downstream services, integrates auth-service login/validation, and hosts consolidated API documentation. |
| **auth-service** | Handles authentication and authorization. Issues and validates JWTs; a JWT validation filter is applied at the gateway level. |
| **patient-service** | Core service for patient records — create, read, update, and delete patient data. Includes request DTOs, service-layer business logic, custom exception handling (e.g., duplicate email), and a Kafka producer for downstream events. |
| **billing-service** | Manages billing operations for patients. Communicates with patient-service over gRPC and runs as an independently dockerized service. |
| **analytics-service** | Consumes patient-related events from Kafka to support downstream analytics and reporting. |
| **infrastructure** | Infrastructure-as-Code (AWS CDK) defining the production environment: ECS cluster and services, MSK Kafka cluster, RDS databases, a load-balanced application gateway, and database health checks. |
| **integration-tests** | End-to-end integration test suites covering patient-service and auth-service flows. |

## Tech Stack

- **Language/Framework:** Java, Spring Boot
- **Inter-service Communication:** gRPC (service-to-service), REST (client-facing via API Gateway)
- **Messaging:** Apache Kafka (via AWS MSK) for event-driven communication between patient-service and analytics-service
- **Auth:** JWT-based authentication and validation
- **Database:** Amazon RDS
- **Containerization:** Docker (each service is independently dockerized)
- **Infrastructure as Code:** AWS CDK (ECS, MSK, RDS, Application Load Balancer)
- **API Documentation:** OpenAPI docs served via the API Gateway
- **Testing:** Integration test suites per service

## Key Features

- Patient CRUD operations with robust request validation and centralized exception handling
- Service-to-service communication over gRPC
- Event-driven architecture using Kafka for decoupled billing/analytics workflows
- JWT-based authentication enforced at the API Gateway
- Fully containerized services with Docker
- Production-ready AWS infrastructure defined as code (ECS Fargate/EC2, MSK, RDS, ALB)
- Integration test coverage for critical service flows

## Project Structure

```
.
├── analytics-service/     # Kafka consumer for patient/billing events
├── api-gateway/            # Entry point, routing, auth integration, API docs
├── auth-service/            # Authentication, JWT issuing & validation
├── billing-service/         # Billing logic, gRPC client to patient-service
├── infrastructure/          # AWS CDK infrastructure as code
├── integration-tests/       # Cross-service integration tests
├── patient-service/         # Core patient data service
├── .gitattributes
└── .gitignore
```

## Getting Started

### Prerequisites

- Java 17+ and Maven/Gradle
- Docker & Docker Compose
- AWS CLI configured (for infrastructure deployment)
- Node.js (for AWS CDK)

### Running Locally

Each service can be built and run independently via Docker:

```bash
# Build and run a given service
cd <service-name>
docker build -t <service-name> .
docker run -p <port>:<port> <service-name>
```

### Deploying Infrastructure

Infrastructure is managed via AWS CDK in the `infrastructure/` directory:

```bash
cd infrastructure
cdk deploy
```

This provisions the ECS cluster, ECS services, MSK Kafka cluster, RDS databases, and the load-balanced application gateway.

### Running Integration Tests

```bash
cd integration-tests
# run the test suite for the relevant service
```

## Roadmap

- [ ] Expand analytics-service reporting capabilities
- [ ] Add CI/CD pipeline for automated deployments
- [ ] Extend billing-service functionality
- [ ] Add monitoring/observability (metrics, tracing, alerting)
