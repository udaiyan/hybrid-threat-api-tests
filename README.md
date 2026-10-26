# Hybrid Threat API Tests

A synthetic Java API testing portfolio project demonstrating REST API automation, contract validation, SQL persistence checks, security testing, and lightweight performance testing. 

> **Note:** All data, endpoints, and threat intelligence concepts in this repository are entirely synthetic and created for demonstration purposes only. They do not represent any real systems, clients, or sensitive data.

## Technology Stack

- **Language:** Java 21
- **Build Tool:** Maven
- **Testing Frameworks:** JUnit 5, REST Assured, AssertJ
- **Validation:** JSON Schema Validator, OpenAPI/Swagger Contract Validator
- **Mocking:** WireMock
- **Application & Database:** Spring Boot, PostgreSQL, Testcontainers
- **Performance:** k6
- **CI/CD:** GitHub Actions

## Project Structure

    .
    ├── .github/workflows/ci.yml       # GitHub Actions CI pipeline
    ├── perf/                          # k6 performance and load testing scripts
    ├── src/main/java/                 # Synthetic Spring Boot API and domain models
    ├── src/test/java/                 # Test suites (mock and app profiles)
    └── src/test/resources/            # OpenAPI specs, JSON schemas, and test data

## Running the Tests

This project uses Maven profiles to separate lightweight mock tests from full integration tests.

### 1. Mock API Tests (Recommended for quick validation)
Runs tests against a WireMock server. No Docker required.

    mvn clean verify -Pmock

### 2. Application & Database Tests
Runs tests against a real Spring Boot application and a PostgreSQL database spun up via Testcontainers. **Requires Docker to be running.**

    mvn clean verify -Papp

*(Note: On Windows, if you encounter Docker API version conflicts with Testcontainers, the pom.xml is configured to automatically set the required environment variables for the test run).*

### 3. Running the API Locally
To manually test the API endpoints:

    docker compose up -d
    mvn spring-boot:run

Access endpoints at `http://localhost:8080/api/v1/...`

### 4. Performance Testing
A lightweight k6 smoke test is included to demonstrate basic load testing awareness and threshold assertions.

    k6 run perf/k6-smoke.js

## Capability Mapping

This repository is designed to demonstrate specific test automation engineering capabilities relevant to enterprise API and data testing roles:

| Capability | Evidence in this Repository |
| :--- | :--- |
| **API Testing** | Comprehensive REST Assured test suites validating status codes, headers, and payloads. |
| **Java & OOP** | Clean, maintainable Java 21 code using Records for immutable data transfer objects. |
| **SQL Validation** | Tests that verify API actions by directly querying the PostgreSQL database via JdbcTemplate. |
| **Contract Testing** | Automated validation of requests and responses against an OpenAPI 3.0 specification. |
| **Negative Testing** | Dedicated test classes for missing fields, invalid enums, malformed JSON, and bad IDs. |
| **Large Dataset Handling** | Tests validating pagination, sorting, and filtering query parameters. |
| **Security Testing** | Tests for a protected endpoint demonstrating synthetic PASETO-style bearer token validation and rejection of invalid/missing tokens. |
| **Performance Testing** | A k6 script demonstrating threshold-based performance assertions (e.g., p95 latency). |
| **CI Integration** | GitHub Actions workflow configured to run both test profiles on push and pull requests. |
| **Maintainability** | Reusable test data builders, clear package structure, and separation of mock vs. integration concerns. |

## Limitations & Future Improvements

- The PASETO authentication implementation is simplified for demonstration purposes. It uses a static synthetic token validator and does not represent production-grade cryptographic verification.
- The performance test is a basic smoke test. A full non-functional testing strategy would include extended endurance and stress profiles.
- Future iterations could include mutation testing (e.g., Pitest) and deeper accessibility testing for API error messages.

## License

This project is for portfolio demonstration purposes.
