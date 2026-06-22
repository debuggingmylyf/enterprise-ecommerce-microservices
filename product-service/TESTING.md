# Product Service – API Testing Document

Base URL: `http://localhost:{port}`  
Content-Type: `application/json`

---

## 1. Category Endpoints `/api/v1/categories`

### 1.1 Create Category (Root)
**POST** `/api/v1/categories`  
**Expected:** `201 Created`

```json
{
  "name": "Electronics",
  "description": "All electronic devices and accessories"
}
```

### 1.2 Create Category (With Parent)
**POST** `/api/v1/categories`  
**Expected:** `201 Created`

```json
{
  "name": "Smartphones",
  "description": "Mobile phones and accessories",
  "parentCategoryId": "{{categoryId}}"
}
```

### 1.3 Create Category – Validation Failure (blank name)
**POST** `/api/v1/categories`  
**Expected:** `400 Bad Request`

```json
{
  "name": "",
  "description": "Some description"
}
```

### 1.4 Get All Categories
**GET** `/api/v1/categories`  
**Expected:** `200 OK` – array of CategoryResponse

### 1.5 Get Category by ID
**GET** `/api/v1/categories/{{categoryId}}`  
**Expected:** `200 OK`

### 1.6 Get Category – Not Found
**GET** `/api/v1/categories/00000000-0000-0000-0000-000000000000`  
**Expected:** `404 Not Found`

### 1.7 Update Category
**PUT** `/api/v1/categories/{{categoryId}}`  
**Expected:** `200 OK`

```json
{
  "name": "Consumer Electronics",
  "description": "Updated description for electronics",
  "parentCategoryId": null
}
```

### 1.8 Update Category – Validation Failure (name exceeds 100 chars)
**PUT** `/api/v1/categories/{{categoryId}}`  
**Expected:** `400 Bad Request`

```json
{
  "name": "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
  "description": "Too long name"
}
```

### 1.9 Delete Category
**DELETE** `/api/v1/categories/{{categoryId}}`  
**Expected:** `204 No Content`

### 1.10 Delete Category – Not Found
**DELETE** `/api/v1/categories/00000000-0000-0000-0000-000000000000`  
**Expected:** `404 Not Found`

---

## 2. Product Endpoints `/api/v1/products`

### 2.1 Create Product (Full Payload)
**POST** `/api/v1/products`  
**Expected:** `201 Created`

```json
{
  "name": "Apple iPhone 15 Pro",
  "shortDescription": "Latest iPhone with A17 Pro chip and titanium design",
  "fullDescription": "The iPhone 15 Pro features a titanium design, A17 Pro chip, 48MP main camera, and USB-C connectivity. Available in 128GB, 256GB, and 512GB storage options.",
  "brand": "Apple",
  "categoryId": "{{categoryId}}",
  "basePrice": 134900.00,
  "discountPrice": 129900.00,
  "images": [
    {
      "imageUrl": "https://cdn.example.com/iphone15pro-front.jpg",
      "displayOrder": 1,
      "primary": true
    },
    {
      "imageUrl": "https://cdn.example.com/iphone15pro-back.jpg",
      "displayOrder": 2,
      "primary": false
    }
  ],
  "attributes": [
    {
      "attributeName": "Color",
      "attributeValue": "Natural Titanium"
    },
    {
      "attributeName": "Storage",
      "attributeValue": "256GB"
    },
    {
      "attributeName": "RAM",
      "attributeValue": "8GB"
    }
  ]
}
```

### 2.2 Create Product (Minimal Required Fields)
**POST** `/api/v1/products`  
**Expected:** `201 Created`

```json
{
  "name": "Samsung Galaxy S24",
  "shortDescription": "Flagship Android smartphone",
  "brand": "Samsung",
  "categoryId": "{{categoryId}}",
  "basePrice": 79999.00,
  "images": [
    {
      "imageUrl": "https://cdn.example.com/galaxy-s24.jpg",
      "displayOrder": 1,
      "primary": true
    }
  ]
}
```

### 2.3 Create Product – Validation Failure (missing required fields)
**POST** `/api/v1/products`  
**Expected:** `400 Bad Request`

```json
{
  "shortDescription": "Missing name, brand, categoryId, basePrice and images",
  "fullDescription": "This request should fail validation"
}
```

### 2.4 Create Product – Validation Failure (negative price)
**POST** `/api/v1/products`  
**Expected:** `400 Bad Request`

```json
{
  "name": "Test Product",
  "shortDescription": "Short desc",
  "brand": "TestBrand",
  "categoryId": "{{categoryId}}",
  "basePrice": -100.00,
  "images": [
    {
      "imageUrl": "https://cdn.example.com/test.jpg",
      "displayOrder": 1,
      "primary": true
    }
  ]
}
```

### 2.5 Get All Products (Default Pagination)
**GET** `/api/v1/products`  
**Expected:** `200 OK` – paginated response

### 2.6 Get All Products (Custom Pagination & Sorting)
**GET** `/api/v1/products?page=0&size=5&sortBy=name&direction=ASC`  
**Expected:** `200 OK`

### 2.7 Get Product by ID
**GET** `/api/v1/products/{{productId}}`  
**Expected:** `200 OK`

### 2.8 Get Product by ID – Not Found
**GET** `/api/v1/products/00000000-0000-0000-0000-000000000000`  
**Expected:** `404 Not Found`

### 2.9 Get Product by Slug
**GET** `/api/v1/products/slug/apple-iphone-15-pro`  
**Expected:** `200 OK`

### 2.10 Get Product by Slug – Not Found
**GET** `/api/v1/products/slug/non-existent-slug`  
**Expected:** `404 Not Found`

### 2.11 Update Product
**PUT** `/api/v1/products/{{productId}}`  
**Expected:** `200 OK`

```json
{
  "name": "Apple iPhone 15 Pro Max",
  "shortDescription": "Largest iPhone with A17 Pro chip",
  "fullDescription": "Updated full description with all new features.",
  "brand": "Apple",
  "categoryId": "{{categoryId}}",
  "basePrice": 159900.00,
  "discountPrice": 149900.00
}
```

### 2.12 Update Product – Validation Failure (blank name)
**PUT** `/api/v1/products/{{productId}}`  
**Expected:** `400 Bad Request`

```json
{
  "name": "",
  "shortDescription": "Some description",
  "brand": "Apple",
  "basePrice": 99999.00
}
```

### 2.13 Update Product Status – DRAFT → ACTIVE
**PATCH** `/api/v1/products/{{productId}}/status`  
**Expected:** `200 OK`

```json
{
  "status": "ACTIVE"
}
```

### 2.14 Update Product Status – ACTIVE → INACTIVE
**PATCH** `/api/v1/products/{{productId}}/status`  
**Expected:** `200 OK`

```json
{
  "status": "INACTIVE"
}
```

### 2.15 Update Product Status – ACTIVE → DISCONTINUED
**PATCH** `/api/v1/products/{{productId}}/status`  
**Expected:** `200 OK`

```json
{
  "status": "DISCONTINUED"
}
```

### 2.16 Update Product Status – Validation Failure (invalid status)
**PATCH** `/api/v1/products/{{productId}}/status`  
**Expected:** `400 Bad Request`

```json
{
  "status": "INVALID_STATUS"
}
```

### 2.17 Update Product Status – Null status
**PATCH** `/api/v1/products/{{productId}}/status`  
**Expected:** `400 Bad Request`

```json
{
  "status": null
}
```

### 2.18 Delete Product
**DELETE** `/api/v1/products/{{productId}}`  
**Expected:** `204 No Content`

### 2.19 Delete Product – Not Found
**DELETE** `/api/v1/products/00000000-0000-0000-0000-000000000000`  
**Expected:** `404 Not Found`

---

## 3. Search Endpoints `/api/v1/search`

### 3.1 Search – All Products (No Filters)
**GET** `/api/v1/search/products`  
**Expected:** `200 OK` – paginated response of all products

### 3.2 Search – By Keyword
**GET** `/api/v1/search/products?keyword=iPhone`  
**Expected:** `200 OK` – products matching name, short description or brand

### 3.3 Search – By Category
**GET** `/api/v1/search/products?categoryId={{categoryId}}`  
**Expected:** `200 OK` – products in that category

### 3.4 Search – By Brand (case-insensitive)
**GET** `/api/v1/search/products?brand=apple`  
**Expected:** `200 OK`

### 3.5 Search – By Price Range
**GET** `/api/v1/search/products?minPrice=50000&maxPrice=150000`  
**Expected:** `200 OK` – products with base price between 50000 and 150000

### 3.6 Search – By Status
**GET** `/api/v1/search/products?status=ACTIVE`  
**Expected:** `200 OK` – only ACTIVE products

### 3.7 Search – Combined Filters
**GET** `/api/v1/search/products?keyword=Samsung&brand=Samsung&minPrice=30000&maxPrice=100000&status=ACTIVE&categoryId={{categoryId}}&page=0&size=10&sortBy=basePrice&direction=ASC`  
**Expected:** `200 OK`

### 3.8 Search – Invalid Status Value
**GET** `/api/v1/search/products?status=UNKNOWN`  
**Expected:** `400 Bad Request`

### 3.9 Search – Custom Pagination
**GET** `/api/v1/search/products?page=1&size=5&sortBy=name&direction=ASC`  
**Expected:** `200 OK` – second page with 5 results sorted by name ascending

---

## 4. Expected Response Shapes

### CategoryResponse
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "parentCategoryId": null,
  "name": "Electronics",
  "description": "All electronic devices and accessories",
  "active": true
}
```

### CreateProductResponse
```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "name": "Apple iPhone 15 Pro",
  "slug": "apple-iphone-15-pro",
  "skuCode": "SKU-XXXXXXXX",
  "status": "DRAFT"
}
```

### ProductResponse (Full)
```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "name": "Apple iPhone 15 Pro",
  "slug": "apple-iphone-15-pro",
  "shortDescription": "Latest iPhone with A17 Pro chip and titanium design",
  "fullDescription": "The iPhone 15 Pro features...",
  "brand": "Apple",
  "skuCode": "SKU-XXXXXXXX",
  "status": "ACTIVE",
  "category": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "parentCategoryId": null,
    "name": "Smartphones",
    "description": "Mobile phones and accessories",
    "active": true
  },
  "pricing": {
    "basePrice": 134900.00,
    "discountPrice": 129900.00,
    "currency": "INR"
  },
  "images": [
    {
      "imageUrl": "https://cdn.example.com/iphone15pro-front.jpg",
      "displayOrder": 1,
      "primary": true
    }
  ],
  "attributes": [
    {
      "attributeName": "Color",
      "attributeValue": "Natural Titanium"
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### PaginatedResponse
```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "last": false
}
```

---

## 5. Product Status Lifecycle

```
DRAFT → ACTIVE → INACTIVE
              ↘ DISCONTINUED
```

- New products always start as `DRAFT`
- Valid status values: `DRAFT`, `ACTIVE`, `INACTIVE`, `DISCONTINUED`

---

## 6. Recommended Test Execution Order

1. **Create root category** → capture `categoryId`
2. **Create sub-category** using `categoryId` as `parentCategoryId`
3. **Create product** using `categoryId` → capture `productId` and `slug`
4. **Get product by ID** using `productId`
5. **Get product by slug**
6. **Update product** using `productId`
7. **Update product status** (DRAFT → ACTIVE)
8. **Search products** with various filters
9. **Get all products** with pagination
10. **Delete product** using `productId`
11. **Delete category** using `categoryId`
