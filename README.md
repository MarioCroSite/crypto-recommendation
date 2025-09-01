# Crypto Recommendation System

A Spring Boot application that provides cryptocurrency price analysis and recommendations based on normalized price ranges and historical data aggregation.

## 🏗️ Architecture

This project follows **Hexagonal Architecture** (also known as Ports and Adapters pattern), which provides a clean separation of concerns and makes the system more maintainable and testable.

### Architecture Layers
┌─────────────────────────────────────────────────────────────┐
│ Application Layer │
│ ┌─────────────────┐ ┌─────────────────────────────────┐ │
│ │ Controllers │ │ DTOs & Mappers │ │
│ └─────────────────┘ └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────┐
│ Domain Layer │
│ ┌─────────────────┐ ┌─────────────────────────────────┐ │
│ │ Services │ │ Domain Models │ │
│ │ Aggregators │ │ (Entities) │ │
│ │ Evaluators │ │ │ │
│ └─────────────────┘ └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────┐
│ Infrastructure Layer │
│ ┌─────────────────┐ ┌─────────────────────────────────┐ │
│ │ Repositories │ │ JPA Entities │ │
│ │ Mappers │ │ & Database │ │
│ │ Adapters │ │ │ │
│ └─────────────────┘ └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘

### Key Benefits of Hexagonal Architecture

- **Dependency Inversion**: Domain layer doesn't depend on infrastructure
- **Testability**: Easy to unit test business logic in isolation
- **Flexibility**: Can easily swap implementations (e.g., different databases)
- **Maintainability**: Clear separation of concerns
- **Scalability**: Easy to add new features without affecting existing code

## 🚀 Features

- **Cryptocurrency Data Ingestion**: Process CSV and TXT files with historical price data
- **Price Aggregation**: Aggregate prices by time periods (monthly, half-yearly, yearly)
- **Normalized Range Calculation**: Calculate and compare price volatility across different cryptocurrencies
- **Recommendation Engine**: Provide recommendations based on normalized price ranges
- **Real-time Analysis**: Get highest performing symbols for specific dates
- **Caching**: Optimized performance with Spring Cache
- **Rate Limiting**: API protection with Bucket4j
- **Security**: Spring Security integration

## 🛠️ Technology Stack

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.5.4** - Modern Spring framework
- **Spring Data JPA** - Data persistence layer
- **H2 Database** - In-memory database for development
- **MapStruct** - Type-safe object mapping
- **Lombok** - Reduce boilerplate code
- **Spring Security** - Authentication and authorization
- **Bucket4j** - Rate limiting implementation
- **Gradle** - Build tool

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher

