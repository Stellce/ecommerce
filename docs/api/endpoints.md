# Endpoints
Base path: /api

## 1. Auth
- POST /auth/register
  - Request: RegisterRequest
  - Response: AuthResponse
- POST /auth/login 
  - Request: LoginRequest 
  - Response: AuthResponse
- POST /auth/refresh
  - Request: RefreshTokenRequest 
  - Response: AuthResponse

### Schemas
- RegisterRequest:
  - email: string
  - password: string
- LoginRequest:
  - email: string
  - password: string
- RefreshTokenRequest:
  - refreshToken: string
- AuthResponse:
  - access_token: string
  - refresh_token: string

## 2. Products
- GET /products?page=0&size=10 
  - Response: PageResponse<ProductResponse>
- GET /products/{id} 
  - Response: ProductResponse
- POST /products 
  - Request: CreateProductRequest 
  - Response: ProductResponse (201)
- PATCH /products/{id} 
  - Request: PatchProductRequest 
  - Response: ProductResponse
- DELETE /products/{id} 
  - Response: 204 No Content

### Schemas
- ProductResponse
  - id: string
  - name: string
  - description: string
  - price: decimal
  - stock: integer
- CreateProductRequest
  - name: string
  - description: string
  - price: decimal
  - stock: integer
- PatchProductRequest
  - name (optional): string
  - description (optional): string
  - price (optional): decimal
  - stock (optional): integer

## 3. Orders
 User sees only own orders; Admin sees all orders

- GET /orders?page=0&size=10 
  - Response: PageResponse<OrderResponse>
- GET /orders/{id} 
  - Response: OrderResponse
- POST /orders 
  - Request: CreateOrderRequest 
  - Response: OrderResponse (201)
- PATCH /orders/status/{id} 
  - Request: PatchOrderStatusRequest 
  - Response: OrderResponse
- PATCH /orders/{id}/cancel 
  - Response: OrderResponse

### Schemas
- CreateOrderRequest:
  - List<OrderItemRequest> items
    - productId: string
    - quantity: integer
- PatchOrderStatusRequest
  - status: string
- OrderResponse:
  - id: string
  - List<OrderItemResponse> items
    - id: string
    - product: ProductResponse
    - quantity: integer
    - priceAtPurchase: decimal
  - status: string
  - totalPrice: decimal
  - createdAt: (ISO-8601 date-time)
- OrderStatus:
  - CREATED
  - PAID
  - SHIPPED
  - CANCELLED
