# Endpoints

## 1. Auth
- POST /auth/register -> RegisterRequest -> AuthResponse
- POST /auth/login -> LoginRequest -> AuthResponse
- POST /auth/refresh -> RefreshTokenRequest -> AuthResponse

### DTOs
- RegisterRequest:
  - email
  - password
- LoginRequest:
  - email
  - password
- AuthResponse:
  - access_token
  - refresh_token
- RefreshTokenRequest
  - refreshToken

## 2. Products
- GET /products?page=0&size=10 -> PageResponse<ProductResponse>
- GET /products/{id} -> ProductResponse
- POST /products -> ProductCreateRequest -> ProductResponse (201)
- PUT /products/{id} -> ProductUpdateRequest -> 204 No Content
- DELETE /products/{id} -> 204 No Content

### DTOs
- ProductResponse
  - id
  - name
  - description
  - price
  - stock
- ProductCreateRequest
  - name
  - description
  - price
  - stock
- ProductUpdateRequest
  - ?name
  - ?description
  - ?price
  - ?stock

## 3. Orders
- GET /orders?page=0&size=10 -> PageResponse<OrderResponse> // User sees only own orders; Admin sees all orders
- GET /orders/{id} -> OrderResponse
- POST /orders -> OrderCreateRequest -> OrderResponse (201)
- PUT /orders/{id} -> OrderStatusUpdateRequest -> 204 No Content
- DELETE /orders/{id} -> 204 No Content

### DTOs
- OrderResponse
  - id
  - items
    - productId
    - productName
    - quantity
    - priceAtPurchase
  - userEmail
  - status
  - totalPrice
  - createdAt
- OrderCreateRequest
  - items
    - productId
    - quantity
- OrderStatusUpdateRequest
  - status
