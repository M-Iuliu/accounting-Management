GENERAL INFORMATION:

DianaTravel is a Spring Boot-based travel management application that provides CRUD APIs for managing reservations, providers, offers, and clients. It uses Maven, Spring Boot, and PostgreSQL with Liquibase for database versioning.

Features

RESTful API for managing Reservations, Providers, Offers, and Clients

Full CRUD operations: Get by ID, Get all by filter, Create, Update (PUT & PATCH), Delete

Database integration with PostgreSQL using Liquibase

Error handling with custom response messages

Built using Spring Boot 3.x, Maven, and JPA/Hibernate

Prerequisites

Ensure you have the following installed:

Java 17+

Maven 3.8+

PostgreSQL (Ensure database is running)

1) INSTALLATION AND SETUP

 Clone the repository
   https://github.com/M-Iuliu/accounting-Management.git

 Configure Database

Update the application.properties file with your PostgreSQL credentials:
spring.application.name=Accounting and Management System

2. DATA SOURCE

Set here configurations for the database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/dianaTravel
spring.datasource.username=postgres
spring.datasource.password=79.Kcipjxckxq
spring.datasource.driver-class-name=org.postgresql.Driver

Keep the connection alive if idle for a long time (needed in production)
spring.datasource.testWhileIdle=true
spring.datasource.validationQuery=SELECT 1

JPA / HIBERNATE

Show or not log for each sql query
spring.jpa.show-sql=true
#Dialect
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

Hibernate ddl auto (update):
spring.jpa.hibernate.ddl-auto=update

Naming strategy
spring.jpa.properties.hibernate.naming-strategy = org.hibernate.cfg.ImprovedNamingStrategy

Allows Hibernate to generate SQL optimized for a particular DBMS
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

LiquiBase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

3. Build and Run the application:

   mvn clean install;
   mvn spring-boot:run;

4. API Endpoints

The application provides CRUD operations for the following controllers:

ClientController (Example):

| Method | Endpoint | Description |
|--------|----------------|-------------|
| `GET`  | `/getClient/{id}` | Fetch client by ID |
| `GET`  | `/getClients` | Fetch all clients |
| `POST` | `/saveClient` | Create a new client |
| `PUT`  | `/updateClient/{id}` | Update an existing client |
| `PATCH` | `/updateClientPartial/{id}` | Partially update a client |
| `DELETE` | `/deleteClient/{id}` | Delete a client |

Similarly, the ReservationController, ProviderController, and OfferController follow the same structure.
