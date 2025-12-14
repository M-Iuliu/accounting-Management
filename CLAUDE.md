# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Accounting and Management System - A Spring Boot application for managing travel agency operations including clients, reservations, offers, and providers. The system uses PostgreSQL for data persistence and Liquibase for database migrations.

**Technology Stack:**
- Java 17
- Spring Boot 3.3.0
- PostgreSQL 13.12
- Liquibase for database migrations
- Lombok for reducing boilerplate
- Maven for dependency management

## Build and Run Commands

### Local Development with Docker

Start PostgreSQL database:
```bash
docker-compose -f src/main/resources/docker-compose.yaml up -d
```

### Build and Test

Build the project:
```bash
mvn clean install
```

Run tests:
```bash
mvn test
```

Run a single test class:
```bash
mvn test -Dtest=ClassName
```

Run a single test method:
```bash
mvn test -Dtest=ClassName#methodName
```

### Run Application

```bash
mvn spring-boot:run
```

### Database Management

Generate Liquibase changelog from existing database:
```bash
mvn liquibase:generateChangeLog
```

Update database to latest version:
```bash
mvn liquibase:update
```

Rollback last change:
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## Architecture

### Package Structure

The application follows a standard layered architecture:

- `entity/` - JPA entities (Client, Reservation, Offer, Provider, Observation)
- `repository/` - Spring Data JPA repositories for database access
- `service/` - Business logic layer, organized by domain (clients, reservations, offers, providers)
  - Each service domain has a main service class and a helper class
- `controller/` - REST API endpoints
- `dto/` - Data Transfer Objects for API contracts
- `constants/` - Application-wide constants

### Data Model Relationships

**Core Entities:**
- **Client** - Customer information with references to offers and reservations
- **Offer** - Travel proposals with pricing details, linked to clients and observations
- **Reservation** - Confirmed bookings with travel details, dates, and payment tracking
- **Provider** - Travel service providers
- **Observation** - Comments/notes associated with offers

**Key Relationships:**
- Clients can have multiple offers and reservations (stored as IDs)
- Offers contain commission calculations and status tracking
- Reservations track payment deadlines and remaining costs
- Observations are linked to offers for additional notes

### Database Configuration

**Active Database:** PostgreSQL (localhost:5432)
- Application uses database: `postgres` (per application.properties)
- Liquibase configuration references: `dianaTravel` (per liquibase.properties)
- Docker container: `postgres-dianaTravel`

**Important:** There is a mismatch between database names in configuration files. Verify which database name should be used when making changes.

### Liquibase Migration Structure

Database changes are managed through Liquibase:
- Master changelog: `src/main/resources/db/changelog/db.changelog-master.yaml`
- Initial schema: `db-init.xml`
- Updates: `db-updates.xml`

All schema changes must be added as new changesets in the appropriate XML file.

## Development Notes

### Lombok Usage

All entities use Lombok annotations (`@Getter`, `@Setter`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@ToString`). When modifying entities, maintain this pattern.

### JPA Configuration

- Hibernate DDL mode: `update` (auto-updates schema based on entities)
- SQL logging is enabled (`spring.jpa.show-sql=true`)
- Using PostgreSQL dialect

### Service Layer Pattern

Services are organized by domain with a two-class pattern:
- Main service class (e.g., `ClientsService`)
- Helper class (e.g., `ClientsServiceHelper`)

This separation typically places core business logic in the service and utility/validation methods in the helper.
