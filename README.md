# Coffee Inventory Management System
This repository serves as a practical application of the advanced concepts mastered through the Spring Framework Guru courses. The primary objective is to move beyond tutorial-based learning by architecting a real-world Business-to-Business (B2B) inventory solution using modern Java standards.

## Project Purpose
The Coffee Inventory Tracker is designed as a Back-Office ERP (Enterprise Resource Planning) tool. It handles the complex logic of tracking coffee bean stock, managing suppliers, and calculating consumption rates. The project demonstrates the ability to build clean, maintainable, and highly tested backend services.

## Core Learnings Applied
This project synthesizes the curriculum from three specialized Udemy courses:

- **Spring Framework 6 & Spring Boot 3**: Implemented using Dependency Injection, MapStruct for DTO mapping, and Spring Data JPA with PostgreSQL.

- **Spring Security Core**: Architected with a stateless JWT-based authentication system, using granular Role-Based Access Control (RBAC) and custom security meta-annotations.

- **Testing Spring Boot**: Developed using a strict Test-Driven Development (TDD) approach, utilizing JUnit 5, AssertJ, BDD Mockito, and MockMvc with JsonPath for web slice verification.

## Technical Architecture
The system follows a modular architecture to ensure scalability and separation of concerns:

- **Persistence**: Managed via Flyway migrations to ensure database versioning and consistency across environments.

- **Security**: Leverages the modern Spring Security Lambda DSL and OAuth2 Resource Server configuration.

- **Domain Modeling**: Built with a professional entity relationship structure, separating bean catalogs from physical stock records.

- **Validation**: Implementation of robust Bean Validation (JSR 380) to ensure data integrity at the API entry point. 