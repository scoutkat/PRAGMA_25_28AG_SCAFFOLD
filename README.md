# CrediYa Platform - Microservices Architecture

CrediYa is a digital platform that seeks to digitize and optimize the management of personal loan applications, eliminating the need for manual and in-person processes.

## Architecture Overview

The platform is built using a microservices architecture with the following key components:

- **Authentication Service**: Handles user registration and validation
- **Loan Requests Service**: Manages loan applications and processing
- **PostgreSQL Databases**: R2DBC for reactive database access
- **Docker & Docker Compose**: Containerization and orchestration

## Technology Stack

- **Java 17**: Programming language
- **Spring Boot 3.2.0**: Application framework
- **Spring WebFlux**: Reactive programming
- **R2DBC**: Reactive database access
- **PostgreSQL**: Relational database
- **Maven**: Build tool
- **Docker**: Containerization
- **OpenAPI/Swagger**: API documentation
- **JUnit 5**: Testing framework

## Project Structure

```
├── authentication-service/          # User authentication microservice
│   ├── src/main/java/com/crediya/authentication/
│   │   ├── application/service/     # Application services
│   │   ├── domain/                  # Domain models and ports
│   │   └── infrastructure/          # Infrastructure adapters
│   ├── src/test/                   # Unit tests
│   ├── Dockerfile                  # Docker configuration
│   └── pom.xml                     # Maven dependencies
├── loan-requests-service/          # Loan requests microservice
│   ├── src/main/java/com/crediya/loanrequests/
│   │   ├── application/service/     # Application services
│   │   ├── domain/                  # Domain models and ports
│   │   └── infrastructure/          # Infrastructure adapters
│   ├── src/test/                   # Unit tests
│   ├── Dockerfile                  # Docker configuration
│   └── pom.xml                     # Maven dependencies
├── docker-compose.yml              # Local development orchestration
└── README.md                       # This file
```

## Features Implemented

### 1. User Registration (Authentication Service)
- **Endpoint**: `POST /api/v1/usuarios`
- **Description**: Register new users with personal information
- **Validation**: Email uniqueness, required fields, data format validation
- **Response**: User information with generated ID

### 2. Loan Request Creation (Loan Requests Service)
- **Endpoint**: `POST /api/v1/solicitud`
- **Description**: Create new loan applications
- **Validation**: User existence, loan type validity, amount and term validation
- **Response**: Loan request information with calculated details

### 3. User Validation
- **Endpoint**: `GET /api/v1/usuarios/validate?email={email}`
- **Description**: Validate user existence for inter-service communication
- **Response**: Boolean indicating user existence

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- PostgreSQL 15 (if running locally without Docker)

### Local Development with Docker

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd crediya-platform
   ```

2. **Start all services with Docker Compose**
   ```bash
   docker-compose up --build
   ```

3. **Access the services**
   - Authentication Service: http://localhost:8081
   - Loan Requests Service: http://localhost:8082
   - Swagger UI Authentication: http://localhost:8081/swagger-ui.html
   - Swagger UI Loan Requests: http://localhost:8082/swagger-ui.html

### Manual Setup (Without Docker)

1. **Start PostgreSQL databases**
   ```bash
   # Create databases
   createdb crediya_auth
   createdb crediya_loans
   ```

2. **Run Authentication Service**
   ```bash
   cd authentication-service
   mvn spring-boot:run
   ```

3. **Run Loan Requests Service**
   ```bash
   cd loan-requests-service
   mvn spring-boot:run
   ```

## API Documentation

### Authentication Service Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/usuarios` | Register a new user |
| GET | `/api/v1/usuarios/validate` | Validate user existence |
| GET | `/api/v1/usuarios` | Get user by email |

### Loan Requests Service Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/solicitud` | Create a new loan request |
| GET | `/api/v1/solicitud/{id}` | Get loan request by ID |
| GET | `/api/v1/solicitud` | Get loan request by user email |

## Testing

### Run Unit Tests

```bash
# Authentication Service
cd authentication-service
mvn test

# Loan Requests Service
cd loan-requests-service
mvn test
```

### Run Integration Tests

```bash
# Authentication Service
cd authentication-service
mvn verify

# Loan Requests Service
cd loan-requests-service
mvn verify
```

## Database Schema

### Authentication Service (crediya_auth)
- **users**: User information and personal data

### Loan Requests Service (crediya_loans)
- **loan_types**: Available loan products
- **loan_requests**: Loan applications and their status

## Configuration

### Environment Variables

#### Authentication Service
- `SPRING_R2DBC_URL`: Database connection URL
- `SPRING_R2DBC_USERNAME`: Database username
- `SPRING_R2DBC_PASSWORD`: Database password
- `SERVER_PORT`: Service port (default: 8081)

#### Loan Requests Service
- `SPRING_R2DBC_URL`: Database connection URL
- `SPRING_R2DBC_USERNAME`: Database username
- `SPRING_R2DBC_PASSWORD`: Database password
- `EXTERNAL_SERVICES_AUTHENTICATION_BASE_URL`: Authentication service URL
- `SERVER_PORT`: Service port (default: 8082)

## Architecture Principles

### Hexagonal Architecture
- **Domain Layer**: Business logic and entities
- **Application Layer**: Use cases and services
- **Infrastructure Layer**: External adapters and frameworks

### Reactive Programming
- **WebFlux**: Non-blocking HTTP handling
- **R2DBC**: Reactive database access
- **Mono/Flux**: Reactive data streams

### Microservices Communication
- **HTTP/REST**: Synchronous communication
- **WebClient**: Reactive HTTP client
- **Service Discovery**: Docker Compose networking

## Development Guidelines

### Code Quality
- **SonarLint**: Code quality analysis
- **JUnit 5**: Comprehensive unit testing
- **Reactive Testing**: StepVerifier for WebFlux testing

### Git Workflow
- **GitFlow**: Feature branch development
- **Conventional Commits**: Standardized commit messages
- **Pull Requests**: Code review process

## Monitoring and Observability

### Health Checks
- **Actuator**: Spring Boot Actuator endpoints
- **Docker Health Checks**: Container health monitoring
- **Database Health**: Connection pool monitoring

### Logging
- **Structured Logging**: JSON format for production
- **Log Levels**: DEBUG, INFO, WARN, ERROR
- **Request Tracing**: Unique request identifiers

## Deployment

### Docker Deployment
```bash
# Build images
docker build -t crediya/auth-service ./authentication-service
docker build -t crediya/loan-service ./loan-requests-service

# Run containers
docker run -d --name auth-service -p 8081:8081 crediya/auth-service
docker run -d --name loan-service -p 8082:8082 crediya/loan-service
```

### AWS ECS Deployment
- **Fargate**: Serverless container execution
- **API Gateway**: Request routing and load balancing
- **RDS**: Managed PostgreSQL databases
- **ECR**: Container image registry

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions, please contact the development team at dev@crediya.com or create an issue in the repository.