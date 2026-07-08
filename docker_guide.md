# 🐳 Docker Guide — Enterprise E-Commerce Microservices

A complete reference for building, running, and troubleshooting the full microservices stack using Docker.

---

## Architecture Overview

```
                          ┌─────────────────┐
                          │   API Gateway   │  :8282
                          │  (Spring Cloud) │
                          └────────┬────────┘
                                   │  routes via Eureka (lb://)
              ┌────────────────────┼────────────────────┐
              │                    │                    │
     ┌────────┴──────┐   ┌────────┴──────┐   ┌────────┴──────┐
     │  auth-service │   │product-service│   │inventory-svc  │
     │    :8081      │   │    :8082      │   │    :8083      │
     └───────┬───────┘   └───────┬───────┘   └───────┬───────┘
             │                   │                   │
        auth-postgres       product-postgres    inventory-postgres
           :5433               :5435               :5436

                    ┌───────────────────────────┐
                    │ discovery-server (Eureka)  │  :8761
                    └───────────────────────────┘
                    ┌───────────────────────────┐
                    │      config-server         │  :8888
                    └───────────────────────────┘
```

---

## Services Summary

| Service | Port | Image | Database |
|---|---|---|---|
| `config-server` | 8888 | `enterprise-ecommerce/config-server:latest` | — |
| `discovery-server` | 8761 | `enterprise-ecommerce/discovery-server:latest` | — |
| `auth-service` | 8081 | `enterprise-ecommerce/auth-service:latest` | PostgreSQL (auth_db) |
| `product-service` | 8082 | `enterprise-ecommerce/product-service:latest` | PostgreSQL (product_db) |
| `inventory-service` | 8083 | `enterprise-ecommerce/inventory-service:latest` | PostgreSQL (inventory_db) |
| `api-gateway` | 8282 | `enterprise-ecommerce/api-gateway:latest` | — |

---

## Prerequisites

- **Docker Desktop** >= 4.x or **Docker Engine** >= 24.x with **Compose v2**
- At least **8 GB of RAM** allocated to Docker (6 JVM processes + 3 Postgres instances)
- Ports `8081`, `8082`, `8083`, `8282`, `8761`, `8888`, `5433`, `5435`, `5436` must be free on the host

Verify your Docker installation:
```bash
docker --version        # Docker version 24+
docker compose version  # Docker Compose version v2.x
```

---

## Quick Start (Full Stack)

Run the entire platform from the **project root**:

```bash
# 1. Clone the repo (if not already done)
git clone <repo-url>
cd enterprise-ecommerce-microservices

# 2. Build all images and start all containers
docker compose up --build -d

# 3. Watch all service logs
docker compose logs -f
```

> First build takes 5-15 minutes — Maven downloads all dependencies inside Docker.
> Subsequent builds are fast due to Docker layer caching.

### Watch startup progress

```bash
# Follow logs for specific service
docker compose logs -f config-server
docker compose logs -f discovery-server

# Check all container statuses
docker compose ps
```

---

## Startup Order

The services start in this strict dependency order (enforced by `depends_on` with `condition: service_healthy`):

```
1. auth-postgres, product-postgres, inventory-postgres  (parallel)
        |  (all healthy)
2. config-server
        |  (healthy)
3. discovery-server
        |  (healthy)
4. auth-service, product-service, inventory-service     (parallel)
        |  (started)
5. api-gateway
```

---

## Environment Variables

All defaults are set in `.env` at the project root. Override any value by editing that file.

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_USER` | `postgres` | Shared DB username |
| `POSTGRES_PASSWORD` | `India@123` | Shared DB password |
| `AUTH_POSTGRES_DB` | `auth_db` | Auth service database name |
| `PRODUCT_POSTGRES_DB` | `product_db` | Product service database name |
| `INVENTORY_POSTGRES_DB` | `inventory_db` | Inventory service database name |
| `AUTH_POSTGRES_PORT` | `5433` | Host port for auth Postgres |
| `PRODUCT_POSTGRES_PORT` | `5435` | Host port for product Postgres |
| `INVENTORY_POSTGRES_PORT` | `5436` | Host port for inventory Postgres |
| `CONFIG_SERVER_PORT` | `8888` | Config server host port |
| `DISCOVERY_SERVER_PORT` | `8761` | Eureka dashboard host port |
| `AUTH_SERVICE_PORT` | `8081` | Auth service host port |
| `PRODUCT_SERVICE_PORT` | `8082` | Product service host port |
| `INVENTORY_SERVICE_PORT` | `8083` | Inventory service host port |
| `API_GATEWAY_PORT` | `8282` | API gateway host port |
| `JWT_SECRET` | (long default) | JWT signing key |
| `JWT_EXPIRATION` | `3600000` | JWT token TTL (ms) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate DDL mode |

---

## Individual Service Commands

Each service has its own `compose.yaml` for standalone development:

```bash
# Product Service (standalone)
cd product-service
docker compose up --build -d

# Auth Service (standalone)
cd auth-service
docker compose up --build -d
```

### Rebuild a single service in the full stack

```bash
# Rebuild only product-service without restarting others
docker compose up --build -d product-service
```

---

## Useful Docker Commands

### View logs

```bash
# All services (follow)
docker compose logs -f

# Specific service
docker compose logs -f product-service

# Last 100 lines
docker compose logs --tail=100 api-gateway
```

### Check container health

```bash
docker compose ps
docker inspect --format='{{.State.Health.Status}}' config-server
```

### Stop and clean up

```bash
# Stop all containers (keep volumes)
docker compose down

# Stop and remove volumes (DELETES all DB data)
docker compose down -v

# Stop and remove images too
docker compose down --rmi all -v
```

### Execute commands inside a container

```bash
# Open a shell in the product-service container
docker exec -it product-service sh

# Connect to the product Postgres database
docker exec -it product-postgres psql -U postgres -d product_db
```

### Force rebuild (bypass cache)

```bash
docker compose build --no-cache
docker compose up -d
```

---

## Service Endpoints

Once all containers are running:

| Service | URL | Description |
|---|---|---|
| **API Gateway** | http://localhost:8282 | Main entry point for all APIs |
| **Eureka Dashboard** | http://localhost:8761 | Service registry UI |
| **Config Server** | http://localhost:8888/product-service/default | Config for product-service |
| **Auth Service** | http://localhost:8081/swagger-ui.html | Auth API docs (direct) |
| **Product Service** | http://localhost:8082/swagger-ui.html | Product API docs (direct) |
| **Inventory Service** | http://localhost:8083/swagger-ui.html | Inventory API docs (direct) |

### Via API Gateway routes

| Route | Target |
|---|---|
| `GET /api/v1/auth/**` | auth-service |
| `GET /api/v1/products/**` | product-service |
| `GET /api/v1/categories/**` | product-service |
| `GET /api/v1/search/**` | product-service |
| `GET /api/v1/inventory/**` | inventory-service |

---

## Troubleshooting

### Container exits immediately after starting

```bash
# Check the exit logs
docker compose logs <service-name>
```

Common causes:
- **Database not ready** — the healthcheck retries 5 times. Check: `docker compose logs auth-postgres`
- **Config server not reachable** — check: `docker compose logs config-server`
- **Port already in use** — change host port in `.env` (e.g. `AUTH_SERVICE_PORT=18081`)

### Maven build fails in Dockerfile

```bash
# Build with verbose output
docker compose build --no-cache --progress=plain product-service 2>&1 | less
```

Common causes:
- **Non-existent version** — verify `pom.xml` uses a real Maven Central version.
  - Valid: Spring Boot `3.5.3`, Spring Cloud `2025.0.0`
- **Network issues** — Maven can't reach Maven Central. Check your Docker network/proxy settings.

### Service starts but crashes (Spring Boot fails)

```bash
docker compose logs product-service | grep -E "ERROR|Exception|Failed"
```

Common causes:
- **Datasource connection refused** — DB container name in JDBC URL must match compose service name
  - Correct: `jdbc:postgresql://product-postgres:5432/product_db`
- **Config server import fails** — all `application.yml` files use `optional:configserver:` prefix

### Eureka shows services as DOWN

Normal during first 30-60 seconds. Eureka has a grace period. Wait and refresh http://localhost:8761.

### API Gateway returns 503

The API Gateway uses load balancing (`lb://SERVICE-NAME`). Check Eureka registry:
```bash
curl http://localhost:8761/eureka/apps
```

---

## Files Created / Modified

### New Files

| File | Description |
|---|---|
| `docker-compose.yml` | Root compose — orchestrates all 6 services |
| `.env` | Default environment variables |
| `config-server/Dockerfile` | Multi-stage build for config-server |
| `discovery-server/Dockerfile` | Multi-stage build for discovery-server (Eureka) |
| `api-gateway/Dockerfile` | Multi-stage build for api-gateway |
| `inventory-service/Dockerfile` | Multi-stage build for inventory-service |
| `*/.dockerignore` | Excludes `target/`, `.git/`, `.idea/` from build context |

### Modified Files

| File | Change |
|---|---|
| `product-service/Dockerfile` | Fixed `EXPOSE 8081` to `EXPOSE 8082` |
| `product-service/pom.xml` | Fixed Spring Boot `3.5.15` to `3.5.3`, Cloud `2025.0.3` to `2025.0.0` |
| `config-server/pom.xml` | Fixed Spring Boot `3.5.15` to `3.5.3` |
| `inventory-service/pom.xml` | Fixed Spring Boot `3.5.16` to `3.5.3`, Cloud `2025.0.3` to `2025.0.0` |
| `product-service/src/main/resources/application.yaml` | Parameterized config server URI |
| `auth-service/src/main/resources/application.yml` | Made config import `optional:`, parameterized URI |
| `api-gateway/src/main/resources/application.yml` | Made config import `optional:`, parameterized URI |
| `inventory-service/src/main/resources/application.yaml` | Parameterized config server URI |
| `auth-service/compose.yaml` | Added `CONFIG_SERVER_URI: ""` for standalone mode |
| `product-service/compose.yaml` | Added `CONFIG_SERVER_URI: ""` for standalone mode |
