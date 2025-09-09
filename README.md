# CrediYa - Digital Loan Platform

CrediYa is a comprehensive digital platform designed to streamline and optimize personal loan request management, eliminating the need for manual and in-person processes.

## 📅 Pragma 25-28 Agosto Implementation

This implementation was completed during the Pragma 25-28 Agosto training period and includes:

- **Complete microservices architecture** with Authentication and Loan Requests services
- **Reactive programming** with WebFlux throughout
- **Hexagonal architecture** with clean separation of concerns
- **Comprehensive testing** with reactive testing patterns
- **Docker containerization** ready for deployment
- **AWS ECS configuration** for production deployment
- **GitFlow branching strategy** implemented
- **Full documentation** and API specifications

**Branch**: `pragma_25_28_agosto` - Contains the complete implementation from the training period.

## Architecture

The platform is built using a **microservices architecture** with the following key components:

- **Authentication Service**: Handles user registration and validation
- **Loan Requests Service**: Manages loan applications and processing
- **PostgreSQL Databases**: R2DBC for reactive data access
- **API Gateway**: Routes requests to appropriate microservices
- **Docker & AWS ECS**: Containerized deployment on AWS Fargate

## Features

### Core Functionality
- **User Registration**: Complete user profile management with validation
- **Loan Request Processing**: Automated loan application handling
- **User Validation**: Inter-service communication for user verification
- **Loan Type Management**: Configurable loan products with different terms
- **Automated Calculations**: Interest and payment calculations
- **Status Tracking**: Real-time loan request status updates

### Technical Features
- **Reactive Programming**: WebFlux for non-blocking operations
- **Hexagonal Architecture**: Clean separation of concerns
- **Comprehensive Testing**: Unit tests with reactive testing patterns
- **API Documentation**: Swagger/OpenAPI integration
- **Health Monitoring**: Actuator endpoints for service health
- **Docker Support**: Containerized deployment ready

## Technology Stack

- **Backend**: Spring Boot 3.2.0, WebFlux, R2DBC
- **Database**: PostgreSQL 15
- **Documentation**: OpenAPI 3.0, Swagger UI
- **Testing**: JUnit 5, Mockito, Reactor Test
- **Containerization**: Docker, Docker Compose
- **Cloud**: AWS ECS, Fargate, RDS, API Gateway
- **Build Tool**: Maven 3.9+

## Prerequisites

- Java 17 or higher
- Maven 3.9 or higher
- Docker and Docker Compose
- PostgreSQL 15 (if running locally)

## Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd crediya-platform
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the services**
   - Authentication Service: http://localhost:8081
   - Loan Requests Service: http://localhost:8082
   - Swagger UI (Auth): http://localhost:8081/swagger-ui.html
   - Swagger UI (Loans): http://localhost:8082/swagger-ui.html

### Manual Setup

1. **Start PostgreSQL databases**
   ```bash
   # Authentication DB
   docker run -d --name postgres-auth \
     -e POSTGRES_DB=crediya_auth \
     -e POSTGRES_USER=crediya_user \
     -e POSTGRES_PASSWORD=crediya_password \
     -p 5432:5432 postgres:15-alpine

   # Loan Requests DB
   docker run -d --name postgres-loans \
     -e POSTGRES_DB=crediya_loans \
     -e POSTGRES_USER=crediya_user \
     -e POSTGRES_PASSWORD=crediya_password \
     -p 5433:5432 postgres:15-alpine
   ```

2. **Run the services**
   ```bash
   # Authentication Service
   cd authentication-service
   ./mvnw spring-boot:run

   # Loan Requests Service (in another terminal)
   cd loan-requests-service
   ./mvnw spring-boot:run
   ```

##API Documentation

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

## Testing

### Run Unit Tests
```bash
# Authentication Service
cd authentication-service
./mvnw test

# Loan Requests Service
cd loan-requests-service
./mvnw test
```

### Run Integration Tests
```bash
# With Docker Compose
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## Docker Commands

### Build Images
```bash
# Authentication Service
docker build -t crediya-authentication-service ./authentication-service

# Loan Requests Service
docker build -t crediya-loan-requests-service ./loan-requests-service
```

### Run Individual Services
```bash
# Authentication Service
docker run -p 8081:8081 \
  -e SPRING_R2DBC_URL=r2dbc:postgresql://host.docker.internal:5432/crediya_auth \
  crediya-authentication-service

# Loan Requests Service
docker run -p 8082:8082 \
  -e SPRING_R2DBC_URL=r2dbc:postgresql://host.docker.internal:5433/crediya_loans \
  -e EXTERNAL_SERVICES_AUTHENTICATION_BASE_URL=http://host.docker.internal:8081 \
  crediya-loan-requests-service
```

## 🌐 Deployment

### AWS ECS Deployment

1. **Build and push images to ECR**
   ```bash
   # Build and tag images
   docker build -t crediya-authentication-service ./authentication-service
   docker build -t crediya-loan-requests-service ./loan-requests-service

   # Tag for ECR
   docker tag crediya-authentication-service:latest ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/crediya-authentication-service:latest
   docker tag crediya-loan-requests-service:latest ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/crediya-loan-requests-service:latest

   # Push to ECR
   docker push ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/crediya-authentication-service:latest
   docker push ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/crediya-loan-requests-service:latest
   ```

2. **Deploy using ECS**
   ```bash
   # Update task definition with your account details
   aws ecs register-task-definition --cli-input-json file://deployment/ecs-task-definition.json

   # Create or update service
   aws ecs create-service --cluster crediya-cluster --service-name crediya-service --task-definition crediya-microservices
   ```

## 📊 Monitoring

### Health Checks
- Authentication Service: http://localhost:8081/actuator/health
- Loan Requests Service: http://localhost:8082/actuator/health

### Metrics
- Authentication Service: http://localhost:8081/actuator/metrics
- Loan Requests Service: http://localhost:8082/actuator/metrics

## 🔧 Configuration

### Environment Variables

#### Authentication Service
- `SPRING_R2DBC_URL`: Database connection URL
- `SPRING_R2DBC_USERNAME`: Database username
- `SPRING_R2DBC_PASSWORD`: Database password

#### Loan Requests Service
- `SPRING_R2DBC_URL`: Database connection URL
- `SPRING_R2DBC_USERNAME`: Database username
- `SPRING_R2DBC_PASSWORD`: Database password
- `EXTERNAL_SERVICES_AUTHENTICATION_BASE_URL`: Authentication service URL

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Development Team**: CrediYa Platform Development
- **Email**: dev@crediya.com
- **Website**: https://crediya.com

## 📞 Support

For support and questions, please contact:
- Email: support@crediya.com
- Documentation: https://docs.crediya.com
- Issues: GitHub Issues

---

**CrediYa** - Digitalizing the future of personal loans 🚀