# E-Commerce Backend API

A production-minded backend project built with **Spring Boot**, focused on authentication, role-based access control, product management, and order processing.

This project demonstrates a REST API with domain boundaries, JWT-based security, refresh-token flow, database migrations, validation, and consistent error handling.

## What this project demonstrates

- **Feature-based architecture** with clear separation by domain
- **JWT authentication** with refresh tokens stored in the database
- **Role-based authorization** for `USER` and `ADMIN`
- **Product management API** with pagination and partial updates
- **Order workflow** with ownership rules and status transitions
- **Flyway migrations** for database versioning
- **Global exception handling** with structured API errors
- **DTO-based API design** instead of exposing entities directly

## Tech stack

- **Java 21**
- **Spring Boot 4**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway**
- **Gradle**
- **Docker Compose**
- **Swagger / OpenAPI documentation**

## Domain overview

### Authentication
- Register
- Login
- Refresh access token using refresh token

### Products
- List products with pagination
- Get product by id
- Create product
- Update product fields with `PATCH`
- Delete product

### Orders
- Create order from product items
- Get order by id
- Get paginated orders
- Cancel own order
- Update order status as admin

## Access rules

### USER
- Can browse products
- Can create orders
- Can view only their own orders
- Can cancel their own order when business rules allow it

### ADMIN
- Full product management
- Can view all orders
- Can update order status

## Architecture

The project uses a **feature-based package structure**.

Main domain packages:
- `auth`
- `user`
- `product`
- `order`

Shared packages:
- `security` — JWT filters, authentication setup, principals, access rules
- `common` — shared DTOs, exceptions, pagination, global handlers
- `config` — application configuration
- `bootstrap` — development bootstrap data

This structure keeps business logic isolated by domain and scales better than organizing the code only by technical layers.

## Security design

Authentication is based on:
- short-lived **access token**
- persistent **refresh token** stored in the database

This allows explicit session renewal and gives the backend control over refresh-token lifecycle.

Protected endpoints use:
- `Authorization: Bearer <access_token>`

Authorization combines:
- endpoint-level security rules
- role checks
- ownership checks for user-specific resources such as orders

## API documentation

Project documentation includes:
- `docs/api/endpoints.md` — API overview
- `docs/api/common-dtos.md` — shared response models
- `docs/architecture.md` — architecture overview
- `docs/database.dbml` — database schema overview

Swagger/OpenAPI can be added as the next step to expose the API contract in a fully typed format.

## Example business rules

- A regular user can access only their own orders
- An admin can access all orders
- Product stock is validated during order creation
- Order cancellation is allowed only for specific statuses
- Order status changes are restricted to privileged users

## Running the project

### Prerequisites
- JDK 21
- Docker and Docker Compose

### 1. Start PostgreSQL

```bash
docker compose up -d
```

### 2. Configure environment

Create a `.env` file in the backend module or use the existing configuration pattern from the project.

Typical variables:

```env
POSTGRES_DB=shop
POSTGRES_USER=shop
POSTGRES_PASSWORD=shop
JWT_SECRET=replace-with-base64-256-bit-secret
JWT_ACCESS_EXPIRATION=10m
JWT_REFRESH_EXPIRATION=7d
BOOTSTRAP_ADMIN_EMAIL=admin@example.com
BOOTSTRAP_ADMIN_PASSWORD=admin123
```

### 3. Run the application

```bash
cd backend
./gradlew bootRun
```

The API will be available at:

```text
http://localhost:8080/api
```

## Example endpoints

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`

### Products
- `GET /api/products?page=0&size=10`
- `GET /api/products/{id}`
- `POST /api/products`
- `PATCH /api/products/{id}`
- `DELETE /api/products/{id}`

### Orders
- `GET /api/orders?page=0&size=10`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `PATCH /api/orders/status/{id}`
- `PATCH /api/orders/{id}/cancel`

## Example request

```json
{
  "items": [
    {
      "productId": "2c2cfb35-1e8e-4c16-a580-b67b0f7ccdb6",
      "quantity": 2
    }
  ]
}
```

## Example response

```json
{
  "id": "1f2f9850-5a5d-4302-a3f3-bf1f9a0fbd11",
  "items": [
    {
      "id": 1,
      "product": {
        "id": "2c2cfb35-1e8e-4c16-a580-b67b0f7ccdb6",
        "name": "Mechanical Keyboard",
        "description": "TKL mechanical keyboard",
        "price": 299.99,
        "stock": 8
      },
      "quantity": 2,
      "priceAtPurchase": 299.99
    }
  ],
  "status": "CREATED",
  "totalPrice": 599.98,
  "createdAt": "2026-04-21T18:30:00Z"
}
```

## Database

The database schema is managed with **Flyway migrations**.

Main tables include:
- `users`
- `roles`
- `user_roles`
- `products`
- `orders`
- `order_items`
- `refresh_tokens`

A DBML representation is also included for easier schema review.

## Future updates

Planned improvements that would strengthen the project further:
- automated tests for controllers and services
- containerized app runtime for one-command startup
- CI pipeline for build and test automation
- additional business flows such as categories or payment integration