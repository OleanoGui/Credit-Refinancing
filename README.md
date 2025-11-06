# Credit Refinancing System

Complete credit refinancing system implemented with microservices architecture.

## Architecture

```
Credit-Refinancing/
├── bff/                    # Backend for Frontend
├── simulation-service/     # Simulation Service (TODO)
├── proposal-service/       # Proposal Service (TODO)  
├── formalization-service/  # Formalization Service (TODO)
├── after-sales-service/    # After Sales Service (TODO)
├── gateway/               # API Gateway (TODO)
└── docker-compose.yml     # Services orchestration (TODO)
```

## System Journeys

1. **Simulation**: Creation and calculation of refinancing simulations
2. **Proposal**: Analysis and approval of proposals
3. **Formalization**: Contract and documentation process
4. **After Sales**: Customer support and follow-up

## Technologies

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux** (Reactive Programming)
- **Maven** (Multi-module)
- **Docker** (Containerization)

## How to run

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional)

### Run locally

1. **Complete build:**
```bash
mvn clean install
```

2. **Run BFF:**
```bash
cd bff
mvn spring-boot:run
```

3. **Access documentation:**
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health Check: http://localhost:8080/api/health

## Services Status

- [x] BFF - Backend for Frontend
- [ ] Simulation Service
- [ ] Proposal Service  
- [ ] Formalization Service
- [ ] After Sales Service
- [ ] API Gateway
- [ ] Docker Compose

## Commit Structure

This project follows [Conventional Commits](https://conventionalcommits.org/) pattern:

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `style:` Code formatting
- `refactor:` Code refactoring
- `test:` Tests