# Online Book Store: RESTful API Development

This project is a Spring Boot based RESTful API service for an online bookstore. It includes functionalities for book management, review management, and author management. The project also integrates Redis caching and uses basic authentication for securing endpoints.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
    - [Clone the Repository](#clone-the-repository)
    - [Start PostgreSQL and Redis using Docker Compose](#start-postgresql-and-redis-using-docker-compose)
    - [Application Configuration](#application-configuration)
    - [Run the Application](#run-the-application)
    - [Verify Application Status and Documentation](#verify-application-status-and-documentation)
    - [Run Tests](#run-tests)
- [API Endpoints](#api-endpoints)
    - [Books Management](#books-management)
    - [Review Management](#review-management)
    - [Author Management](#author-management)
- [API-First Approach](#api-first-approach)
- [Caching](#caching)
- [Authentication](#authentication)
- [Postman Collection](#postman-collection)

## Prerequisites
- Java 17
- Maven
- Docker
- Docker Compose

## Setup Instructions

### Clone the Repository

```sh
git clone https://github.com/akash4chandran/online-book-store
cd online-book-store
```

### Start PostgreSQL and Redis using Docker Compose

```sh
docker-compose up -d
```

This will start PostgreSQL and Redis containers as defined in the `docker-compose.yml` file. Make sure that Docker is installed and running on your machine.

### Application Configuration

Ensure that the `application.properties` are correctly configured for connecting to PostgreSQL and Redis:

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres123
spring.jpa.hibernate.ddl-auto=validate

# Redis Configuration
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379

# Basic Authentication
spring.security.user.name=admin
spring.security.user.password=admin
```

### Run the Application

To run the application, use the following Maven command:

```sh
mvn spring-boot:run
```

The application should start and be accessible at `http://localhost:8443`.

### Verify Application Status and Documentation

After the application has started, you can verify its status and access the API documentation using the following endpoints:

- **Swagger API Documentation:** [http://localhost:8443/api/swagger](http://localhost:8443/api/swagger)
- **Health Status:** [http://localhost:8443/actuator/health](http://localhost:8443/actuator/health)

### Run Tests

To run the tests, use the following Maven command:

```sh
mvn test
```

This will run all unit tests and ensure everything is functioning as expected.

## API Endpoints

### Books Management
- **GET /api/books** - Retrieve all books with filters for searching.
- **POST /api/books** - Add a new book to the inventory.
- **GET /api/books/{isbn}** - Retrieve details for a specific book by ISBN.
- **PUT /api/books/{isbn}** - Update details of a book by ISBN.
- **DELETE /api/books/{isbn}** - Remove a book from the inventory by ISBN.

### Review Management
- **POST /api/reviews/{isbn}** - Submit a review for a book.
- **GET /api/reviews/{isbn}** - Retrieve reviews for a book.
- **PUT /api/reviews/{isbn}/{reviewId}** – Update a particular review.

### Author Management
- **GET /api/authors/{authorId}** - Retrieve details for a specific author.

## API-First Approach

The project follows an API-First approach for several reasons:

1. **Defined Contract API First**: By defining the API contract first, we ensure that all stakeholders (developers, testers, clients) have a clear understanding of the API endpoints, request/response formats, and expected behavior.

2. **Facilitates Frontend Development**: Frontend developers can develop and test their applications independently using the API contract. This parallel development reduces bottlenecks and speeds up the development process.

3. **Client/End-User Documentation**: An API-first approach provides clear and detailed API documentation, which can be shared with clients or end-users. This enables them to understand and integrate with the API effectively.

4. **Consistency and Maintenance**: With a well-defined API contract, maintaining and updating the API becomes more manageable. It ensures consistency across different versions of the API, making it easier to implement changes and upgrades without breaking existing functionality.
## Caching

The application integrates Redis caching to improve response times for frequently accessed endpoints. Caching is applied to the retrieval of books, authors, and reviews.

**Cache Invalidation:** Caching is managed in the service layer using `@Cacheable`, `@CachePut`, and `@CacheEvict` annotations to ensure data consistency. Cache invalidation happens whenever there is an update or delete operation on the cached data, ensuring that the cache remains consistent with the database.

## Authentication

The application uses Basic Authentication with Spring Security.
- Username - admin
- Password - admin

## Postman Collection

A Postman collection for all API endpoints is available in the `src/main/resources/postman-collection` folder. You can import this collection into Postman to easily test and interact with the API.

### Note on OAuth2

I am aware of OAuth2, which provides a more secure authentication method compared to Basic Authentication.
Considering the limited time available for this project, Basic Authentication was implemented to ensure timely completion.
Future iterations of the project may include OAuth2 for enhanced security.