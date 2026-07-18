# 🧪 Order Service API Testing Guide

This document provides a comprehensive guide for testing the Order Service endpoints, including required headers, request payloads, response payloads, and example `curl` commands.

---

## 📌 Gateway & Authentication Headers

The `order-service` runs on port `8084` but is routing-integrated with the API Gateway on port `8282`. 
Because security is delegated to the API Gateway, the gateway decodes JWT tokens and forwards the user context as custom HTTP headers. 

When testing the `order-service` directly (or through the API Gateway without a token during manual development), you must supply the following headers:

| Header Name | Purpose | Example Value |
|---|---|---|
| `X-User-Id` | Unique UUID of the authenticated user. | `d3b07384-d113-4956-bc9c-9c7161b96a84` |
| `X-User-Role` | Security role of the user (`ROLE_USER` or `ROLE_ADMIN`). | `ROLE_USER` |
| `X-User-Email` | Email of the authenticated user. | `customer@ecommerce.com` |

---

## 🛍️ Customer Endpoints

### 1. Place a New Order
* **Endpoint:** `POST /api/v1/orders`
* **Access Role:** `ROLE_USER` (Requires `X-User-Id` header)
* **Description:** Initiates stock reservation verification, product pricing matching, creates a new order in `CREATED` status and `PENDING` payment status, and saves order items.

#### 📥 Request JSON Payload
```json
{
  "items": [
    {
      "productId": "4a165b4f-8640-42cf-bb82-628d6174a8c9",
      "quantity": 2
    },
    {
      "productId": "7b275c4f-9640-52cf-cc83-728e7184b9da",
      "quantity": 1
    }
  ],
  "shippingAddress": {
    "shippingName": "Jane Doe",
    "shippingPhone": "+1-555-0199",
    "addressLine1": "123 E-Commerce Way",
    "addressLine2": "Suite 400",
    "city": "San Jose",
    "state": "California",
    "country": "United States",
    "postalCode": "95112"
  }
}
```

#### 📤 Response JSON Payload (201 Created)
```json
{
  "orderId": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
  "orderNumber": "ORD-20260718-8F86AB0D",
  "totalAmount": 259.97,
  "status": "CREATED",
  "paymentStatus": "PENDING"
}
```

#### 💻 Example curl
```bash
curl -X POST http://localhost:8282/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Id: d3b07384-d113-4956-bc9c-9c7161b96a84" \
  -H "X-User-Role: ROLE_USER" \
  -d '{
    "items": [
      {
        "productId": "4a165b4f-8640-42cf-bb82-628d6174a8c9",
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "shippingName": "Jane Doe",
      "shippingPhone": "+1-555-0199",
      "addressLine1": "123 E-Commerce Way",
      "city": "San Jose",
      "state": "California",
      "country": "United States",
      "postalCode": "95112"
    }
  }'
```

---

### 2. Retrieve Order by ID
* **Endpoint:** `GET /api/v1/orders/{id}`
* **Access Role:** `ROLE_USER` (owner only) or `ROLE_ADMIN`
* **Description:** Retrieves detailed configuration of an order and its items.

#### 📤 Response JSON Payload (200 OK)
```json
{
  "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
  "orderNumber": "ORD-20260718-8F86AB0D",
  "userId": "d3b07384-d113-4956-bc9c-9c7161b96a84",
  "totalAmount": 259.97,
  "status": "CREATED",
  "paymentStatus": "PENDING",
  "shippingAddress": {
    "shippingName": "Jane Doe",
    "shippingPhone": "+1-555-0199",
    "addressLine1": "123 E-Commerce Way",
    "addressLine2": "Suite 400",
    "city": "San Jose",
    "state": "California",
    "country": "United States",
    "postalCode": "95112"
  },
  "items": [
    {
      "id": "11a0ab0d-40c2-40f4-8d96-b08e4544cc01",
      "productId": "4a165b4f-8640-42cf-bb82-628d6174a8c9",
      "quantity": 2,
      "price": 129.98,
      "warehouseCode": "WH-EAST-01"
    }
  ],
  "createdAt": "2026-07-18T12:00:00",
  "updatedAt": "2026-07-18T12:00:00"
}
```

#### 💻 Example curl
```bash
curl -X GET http://localhost:8282/api/v1/orders/8f86ab0d-40c2-40f4-8d96-b08e4544ccbf \
  -H "X-User-Id: d3b07384-d113-4956-bc9c-9c7161b96a84" \
  -H "X-User-Role: ROLE_USER"
```

---

### 3. Retrieve Customer's Orders
* **Endpoint:** `GET /api/v1/orders/my-orders`
* **Access Role:** `ROLE_USER` (Requires `X-User-Id` header)
* **Description:** Retrieves summaries of all orders created by the authenticated customer.

#### 📤 Response JSON Payload (200 OK)
```json
[
  {
    "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
    "orderNumber": "ORD-20260718-8F86AB0D",
    "totalAmount": 259.97,
    "status": "CREATED",
    "paymentStatus": "PENDING",
    "createdAt": "2026-07-18T12:00:00"
  }
]
```

#### 💻 Example curl
```bash
curl -X GET http://localhost:8282/api/v1/orders/my-orders \
  -H "X-User-Id: d3b07384-d113-4956-bc9c-9c7161b96a84" \
  -H "X-User-Role: ROLE_USER"
```

---

### 4. Cancel Order (Self-Service)
* **Endpoint:** `PATCH /api/v1/orders/{id}/cancel`
* **Access Role:** `ROLE_USER` (owner only) or `ROLE_ADMIN`
* **Description:** Cancels an order. Transitioning from `CREATED` or `CONFIRMED` to `CANCELLED`. Triggers inventory release logic.

#### 📤 Response JSON Payload (200 OK)
```json
{
  "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
  "orderNumber": "ORD-20260718-8F86AB0D",
  "userId": "d3b07384-d113-4956-bc9c-9c7161b96a84",
  "totalAmount": 259.97,
  "status": "CANCELLED",
  "paymentStatus": "PENDING",
  "shippingAddress": {
    "shippingName": "Jane Doe",
    "shippingPhone": "+1-555-0199",
    "addressLine1": "123 E-Commerce Way",
    "city": "San Jose",
    "state": "California",
    "country": "United States",
    "postalCode": "95112"
  },
  "items": [
    {
      "id": "11a0ab0d-40c2-40f4-8d96-b08e4544cc01",
      "productId": "4a165b4f-8640-42cf-bb82-628d6174a8c9",
      "quantity": 2,
      "price": 129.98,
      "warehouseCode": "WH-EAST-01"
    }
  ],
  "createdAt": "2026-07-18T12:00:00",
  "updatedAt": "2026-07-18T12:05:00"
}
```

#### 💻 Example curl
```bash
curl -X PATCH http://localhost:8282/api/v1/orders/8f86ab0d-40c2-40f4-8d96-b08e4544ccbf/cancel \
  -H "X-User-Id: d3b07384-d113-4956-bc9c-9c7161b96a84" \
  -H "X-User-Role: ROLE_USER"
```

---

## 👑 Admin Endpoints

### 5. Paginated List of All Orders
* **Endpoint:** `GET /api/v1/orders`
* **Access Role:** `ROLE_ADMIN`
* **Parameters:**
  - `page`: Page index (default: `0`)
  - `size`: Items per page (default: `20`)
  - `sortBy`: Database field name (default: `createdAt`)
  - `direction`: Sorting order (default: `DESC`)

#### 📤 Response JSON Payload (200 OK)
```json
{
  "content": [
    {
      "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
      "orderNumber": "ORD-20260718-8F86AB0D",
      "totalAmount": 259.97,
      "status": "CREATED",
      "paymentStatus": "PENDING",
      "createdAt": "2026-07-18T12:00:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

#### 💻 Example curl
```bash
curl -X GET "http://localhost:8282/api/v1/orders?page=0&size=10&sortBy=createdAt&direction=DESC" \
  -H "X-User-Id: a1102284-d113-4956-bc9c-9c7161b96aaa" \
  -H "X-User-Role: ROLE_ADMIN"
```

---

### 6. Update Order Status
* **Endpoint:** `PATCH /api/v1/orders/{id}/status`
* **Access Role:** `ROLE_ADMIN`
* **Description:** Manually transition order states. Supported states: `CREATED`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`.

#### 📥 Request JSON Payload
```json
{
  "status": "SHIPPED"
}
```

#### 📤 Response JSON Payload (200 OK)
```json
{
  "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
  "orderNumber": "ORD-20260718-8F86AB0D",
  "userId": "d3b07384-d113-4956-bc9c-9c7161b96a84",
  "totalAmount": 259.97,
  "status": "SHIPPED",
  "paymentStatus": "PENDING",
  "shippingAddress": {
    "shippingName": "Jane Doe",
    "shippingPhone": "+1-555-0199",
    "addressLine1": "123 E-Commerce Way",
    "city": "San Jose",
    "state": "California",
    "country": "United States",
    "postalCode": "95112"
  },
  "items": [
    {
      "id": "11a0ab0d-40c2-40f4-8d96-b08e4544cc01",
      "productId": "4a165b4f-8640-42cf-bb82-628d6174a8c9",
      "quantity": 2,
      "price": 129.98,
      "warehouseCode": "WH-EAST-01"
    }
  ],
  "createdAt": "2026-07-18T12:00:00",
  "updatedAt": "2026-07-18T12:10:00"
}
```

#### 💻 Example curl
```bash
curl -X PATCH http://localhost:8282/api/v1/orders/8f86ab0d-40c2-40f4-8d96-b08e4544ccbf/status \
  -H "Content-Type: application/json" \
  -H "X-User-Id: a1102284-d113-4956-bc9c-9c7161b96aaa" \
  -H "X-User-Role: ROLE_ADMIN" \
  -d '{
    "status": "SHIPPED"
  }'
```

---

## 🔒 Internal Callback Endpoints (Cluster Protected)

These endpoints are strictly restricted to inner services (e.g. called by the Payment Service). The gateway requires the role `INTERNAL_SERVICE` to access these routes.

### 7. Mark Payment Successful
* **Endpoint:** `PATCH /api/v1/internal/orders/{id}/payment-success`
* **Access Role:** `INTERNAL_SERVICE`
* **Description:** Moves order status to `CONFIRMED` and payment status to `SUCCESS`.

#### 📤 Response JSON Payload (200 OK)
```json
{
  "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
  "status": "CONFIRMED",
  "paymentStatus": "SUCCESS"
  // ... (address & item arrays included)
}
```

#### 💻 Example curl
```bash
curl -X PATCH http://localhost:8282/api/v1/internal/orders/8f86ab0d-40c2-40f4-8d96-b08e4544ccbf/payment-success \
  -H "X-User-Role: INTERNAL_SERVICE"
```

---

### 8. Mark Payment Failed
* **Endpoint:** `PATCH /api/v1/internal/orders/{id}/payment-failed`
* **Access Role:** `INTERNAL_SERVICE`
* **Description:** Moves order status to `CANCELLED`, payment status to `FAILED`, and calls `inventory-service` to release the reserved stock items.

#### 📤 Response JSON Payload (200 OK)
```json
{
  "id": "8f86ab0d-40c2-40f4-8d96-b08e4544ccbf",
  "status": "CANCELLED",
  "paymentStatus": "FAILED"
  // ... (address & item arrays included)
}
```

#### 💻 Example curl
```bash
curl -X PATCH http://localhost:8282/api/v1/internal/orders/8f86ab0d-40c2-40f4-8d96-b08e4544ccbf/payment-failed \
  -H "X-User-Role: INTERNAL_SERVICE"
```
