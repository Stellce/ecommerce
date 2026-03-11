# Architecture

## Packages Structure (feature-based)

- backend/src/main/java/com/example/backend/
  - auth
    - controller - register/login
    - service - business logic auth
    - repository - repos for User
    - dto - RegisterRequest, LoginRequest, AuthResponse
    - entity - User
  - product
    - controller - CRUD products
    - service
    - repository
    - dto - ProductCreateRequest, ProductResponse
    - entity - Product
  - order
    - controller - CRUD order
    - service
    - repository
    - dto - OrderCreateRequest, OrderResponse
    - entity - Order, OrderItem