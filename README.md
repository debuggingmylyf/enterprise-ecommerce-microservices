# Enterprise E-Commerce Microservices

## Architecture Overview

This is a microservices-based e-commerce platform with centralized authentication and authorization at the API Gateway level.

### Services

1. **Config Server** (Port 8888) - Centralized configuration management
2. **Discovery Server** (Port 8761) - Eureka service registry
3. **API Gateway** (Port 8282) - Single entry point with JWT validation & RBAC
4. **Auth Service** (Port 8081) - User authentication and JWT token generation
5. **Product Service** (Port 8082) - Product and category management
6. **Inventory Service** (Port 8083) - Stock management, reservations & warehouse tracking

## Security Architecture

### Centralized Security Model

All authentication and authorization is handled at the **API Gateway** level:

- **JWT Token Verification**: Gateway validates JWT signature and expiration
- **Role-Based Access Control (RBAC)**: Gateway enforces route-method-role permissions
- **User Context Forwarding**: Gateway forwards authenticated user info to downstream services via headers

### Authentication Flow

```
1. User → POST /api/v1/auth/login → Auth Service
2. Auth Service → Generate JWT with role claim → Return access token
3. User → Request with Bearer token → API Gateway
4. API Gateway → Validate JWT → Check RBAC rules → Forward with headers → Downstream Service
5. Downstream Service → Trust headers → Process request
```

### JWT Token Structure

```json
{
  "sub": "user@example.com",
  "role": "ROLE_ADMIN",
  "token_type": "access",
  "iat": 1234567890,
  "exp": 1234571490,
  "jti": "unique-token-id"
}
```

### RBAC Rules

| Endpoint Pattern | HTTP Method | Required Roles | Description |
|-----------------|-------------|----------------|-------------|
| `/api/v1/auth/**` | ALL | Public | Authentication endpoints |
| `/api/v1/products` | GET | All authenticated | View products |
| `/api/v1/products` | POST | ADMIN, SELLER | Create product |
| `/api/v1/products/**` | PUT, PATCH | ADMIN, SELLER | Update product |
| `/api/v1/products/**` | DELETE | ADMIN | Delete product |
| `/api/v1/categories` | GET | All authenticated | View categories |
| `/api/v1/categories` | POST, PUT, DELETE | ADMIN | Manage categories |
| `/api/v1/inventory` | POST | ADMIN | Create inventory record |
| `/api/v1/inventory/{productId}/adjust` | PATCH | ADMIN | Adjust stock quantity |
| `/api/v1/inventory/{productId}/threshold` | PATCH | ADMIN | Update low-stock threshold |
| `/api/v1/inventory/reserve` | PATCH | INTERNAL_SERVICE | Reserve stock for order |
| `/api/v1/inventory/release` | PATCH | INTERNAL_SERVICE | Release reservation |
| `/api/v1/inventory/confirm` | PATCH | INTERNAL_SERVICE | Confirm stock deduction |
| `/api/v1/inventory/{productId}` | GET | All authenticated | Get inventory by product |
| `/api/v1/inventory/check/{productId}` | GET | All authenticated | Check stock availability |
| `/api/v1/inventory/low-stock` | GET | All authenticated | List low-stock products |

### User Roles

- **ROLE_USER**: Basic authenticated user (read-only access)
- **ROLE_SELLER**: Can create and update products
- **ROLE_ADMIN**: Full access to all resources including inventory management
- **ROLE_INTERNAL_SERVICE**: Service-to-service calls for stock reservation workflows

## Frontend Integration

### CORS Configuration

The API Gateway is configured with CORS support for common frontend frameworks:
- React (port 3000)
- Angular (port 4200)
- Vite (port 5173)

### Error Response Format

All errors return a consistent JSON structure:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/v1/products",
  "timestamp": "2024-01-15T10:30:00"
}
```

### API Endpoints

#### Authentication

**Register User**
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Login**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}

Response:
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

**Refresh Token**
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}
```

#### Products (Authenticated)

**Get All Products**
```http
GET /api/v1/products?page=0&size=20&sortBy=createdAt&direction=DESC
Authorization: Bearer {accessToken}
```

**Create Product** (ADMIN or SELLER)
```http
POST /api/v1/products
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "Product Name",
  "description": "Description",
  "categoryId": "uuid",
  "price": 99.99,
  "stockQuantity": 100
}
```

**Update Product** (ADMIN or SELLER)
```http
PUT /api/v1/products/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "Updated Name",
  "price": 109.99
}
```

**Delete Product** (ADMIN only)
```http
DELETE /api/v1/products/{id}
Authorization: Bearer {accessToken}
```

#### Categories (Authenticated)

**Get All Categories**
```http
GET /api/v1/categories
Authorization: Bearer {accessToken}
```

**Create Category** (ADMIN only)
```http
POST /api/v1/categories
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic items"
}
```

#### Inventory (Authenticated)

**Create Inventory Record** (ADMIN only)
```http
POST /api/v1/inventory
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "productId": "uuid",
  "warehouseCode": "WH-DELHI",
  "initialQuantity": 100,
  "lowStockThreshold": 10
}
```

**Adjust Stock** (ADMIN only)
```http
PATCH /api/v1/inventory/{productId}/adjust
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "adjustmentType": "INCREASE",
  "quantity": 50,
  "reason": "New shipment received"
}
```

> `adjustmentType` accepts: `INCREASE` (adds stock) or `DECREASE` (removes stock, e.g. damaged goods).

**Update Low-Stock Threshold** (ADMIN only)
```http
PATCH /api/v1/inventory/{productId}/threshold?warehouseCode=WH-DELHI&threshold=20
Authorization: Bearer {accessToken}
```

**Reserve Stock** (INTERNAL_SERVICE)
```http
PATCH /api/v1/inventory/reserve
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "productId": "uuid",
  "warehouseCode": "WH-DELHI",
  "quantity": 2
}
```

**Release Reservation** (INTERNAL_SERVICE — order cancelled)
```http
PATCH /api/v1/inventory/release
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "productId": "uuid",
  "warehouseCode": "WH-DELHI",
  "quantity": 2
}
```

**Confirm Stock Deduction** (INTERNAL_SERVICE — order fulfilled)
```http
PATCH /api/v1/inventory/confirm
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "productId": "uuid",
  "warehouseCode": "WH-DELHI",
  "quantity": 2
}
```

**Get Inventory for a Product**
```http
GET /api/v1/inventory/{productId}?warehouseCode=WH-DELHI
Authorization: Bearer {accessToken}
```

**Check Stock Availability**
```http
GET /api/v1/inventory/check/{productId}
Authorization: Bearer {accessToken}
```

**List Low-Stock Products**
```http
GET /api/v1/inventory/low-stock
Authorization: Bearer {accessToken}
```

## Setup Instructions

### Prerequisites

- Java 21
- PostgreSQL
- Maven

### Database Setup

```sql
-- Create databases
CREATE DATABASE auth_db;
CREATE DATABASE product_db;
CREATE DATABASE inventory_db;
```

### Configuration

The JWT secret is configured in `config-server/src/main/resources/configurations/auth-service.yml` and `api-gateway.yml`. 

For production, use environment variables:
```bash
export JWT_SECRET=your-secure-secret-key-at-least-32-characters
```

### Running Services

Start services in this order:

1. **Config Server**
```bash
cd config-server
mvn spring-boot:run
```

2. **Discovery Server**
```bash
cd discovery-server
mvn spring-boot:run
```

3. **API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

4. **Auth Service**
```bash
cd auth-service
mvn spring-boot:run
```

5. **Product Service**
```bash
cd product-service
mvn spring-boot:run
```

6. **Inventory Service**
```bash
cd inventory-service
mvn spring-boot:run
```

### Health Checks

- Config Server: http://localhost:8888/actuator/health
- Discovery Server: http://localhost:8761
- API Gateway: http://localhost:8282/actuator/health
- Auth Service: http://localhost:8081/actuator/health
- Product Service: http://localhost:8082/actuator/health
- Inventory Service: http://localhost:8083/actuator/health
- Inventory Swagger UI: http://localhost:8083/swagger-ui.html

## Development Notes

### Inventory Service — Stock Lifecycle

The inventory service enforces a three-phase stock lifecycle to support saga-style order processing:

```
Reserve:   available -= qty  |  reserved += qty   (order placed)
Release:   reserved -= qty   |  available += qty   (order cancelled)
Confirm:   reserved -= qty                         (order fulfilled)
```

> Negative stock is never allowed. A `reserve` call will fail if `available < requested`.

**Database schema** (`inventory` table):

| Column | Type | Description |
|---|---|---|
| `id` | UUID | Primary key |
| `product_id` | UUID | Reference to product |
| `warehouse_code` | VARCHAR | Logical warehouse identifier (e.g. `WH-DELHI`) |
| `available_quantity` | INT | Stock ready to be sold |
| `reserved_quantity` | INT | Stock held for pending orders |
| `low_stock_threshold` | INT | Alert level (default: 10) |
| `active` | BOOLEAN | Soft-disable flag |
| `version` | BIGINT | Optimistic locking |
| `created_at/updated_at` | TIMESTAMP | Audit timestamps |
| `created_by/updated_by` | VARCHAR | Audit user |

Indexes: `idx_inventory_product_id`, `idx_inventory_warehouse_code`.

### Adding New Microservices

1. Services behind the gateway don't need Spring Security dependencies
2. Use `HeaderAuthenticationFilter` to extract user context from headers
3. Trust the gateway - it has already validated authentication and authorization
4. Add route configuration in `config-server/configurations/api-gateway.yml`

### Adding New RBAC Rules

Edit `api-gateway/src/main/java/com/ecommerce/gateway/filter/RouteValidator.java`:

```java
if (path.contains("/api/v1/your-resource")) {
    if (method == HttpMethod.POST) {
        return "ROLE_ADMIN".equals(userRole);
    }
    return true;
}
```

### Testing with cURL

**Login**
```bash
curl -X POST http://localhost:8282/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

**Access Protected Resource**
```bash
curl -X GET http://localhost:8282/api/v1/products \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Create Inventory Record** (ADMIN)
```bash
curl -X POST http://localhost:8282/api/v1/inventory \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":"YOUR_PRODUCT_UUID","warehouseCode":"WH-DELHI","initialQuantity":100,"lowStockThreshold":10}'
```

**Check Stock Availability**
```bash
curl -X GET http://localhost:8282/api/v1/inventory/check/YOUR_PRODUCT_UUID \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**List Low-Stock Products**
```bash
curl -X GET http://localhost:8282/api/v1/inventory/low-stock \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Troubleshooting

### 401 Unauthorized
- Check if token is expired
- Verify Bearer token format in Authorization header
- Ensure JWT secret matches between auth-service and api-gateway

### 403 Forbidden
- User role doesn't have permission for this operation
- Check RBAC rules in RouteValidator

### Service Not Found
- Verify service is registered with Eureka: http://localhost:8761
- Check service name matches in gateway routes configuration

## Security Best Practices

1. **Never expose downstream services directly** - Always route through API Gateway
2. **Use HTTPS in production** - Configure SSL/TLS certificates
3. **Rotate JWT secrets regularly** - Use environment variables
4. **Short-lived access tokens** - Current: 700 seconds (configurable)
5. **Implement rate limiting** - Add to API Gateway filters
6. **Monitor failed authentication attempts** - Add security logging
7. **Keep dependencies updated** - Regular security patches

## License

This project is for educational purposes.
