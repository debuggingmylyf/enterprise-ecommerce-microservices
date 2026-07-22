# Agent Instructions — Enterprise E-Commerce Microservices

## 📖 Knowledge Base

Before analysing the full project structure, **always check the knowledge base first**:

```
.agents/knowledge/project-architecture-memory.md
```

This file contains a comprehensive pre-analysed reference of every service, entity, endpoint, RBAC rule, inter-service flow, database schema, and coding convention in this project.

---

## 🔄 Mandatory Knowledge Update Policy

**After completing any of the following changes, you MUST update the knowledge file** (`.agents/knowledge/project-architecture-memory.md`) and its `metadata.json`:

### Triggers for Update

| Change Type | What to Update in Memory |
|-------------|--------------------------|
| **New microservice added** | Add full entry in Module Map (§2), Database Schema (§6), Docker (§7.3), Health Checks (§9), File Finder (§11) |
| **New entity/table created** | Update the relevant service's Module Map entry + Database Schema (§6) |
| **New API endpoint added** | Update the relevant service's endpoint table + RBAC Matrix (§4.3) if role-restricted |
| **RBAC rule changed** | Update §4.3 RBAC Matrix + RouteValidator reference |
| **New Feign client / inter-service call** | Update §5 Inter-Service Communication diagram + tables |
| **New shared library module** | Update §3 Shared Library table |
| **Database schema change** | Update §6 Database Schema Summary |
| **Port / config change** | Update §7 Configuration & Environment |
| **New enum values added** | Update the relevant enum listing in the Module Map |
| **Docker Compose change** | Update §7.3 Docker Compose table |
| **Architecture pattern change** | Update §8 Patterns & Conventions |
| **New future/planned service** | Update §10 Future / Planned Services |

### Update Checklist

When updating the knowledge file, follow this checklist:

1. ✅ Update the relevant section(s) in `project-architecture-memory.md`
2. ✅ Update the `lastUpdated` date in `metadata.json`
3. ✅ If new source files are referenced, add them to `references` array in `metadata.json`
4. ✅ If new tags are relevant, add them to `tags` array in `metadata.json`
5. ✅ Verify the architecture diagram (§1) is still accurate

### How to Update

- **DO NOT** rewrite the entire file. Make targeted edits to the affected sections only.
- **DO** preserve the existing structure and section numbering.
- **DO** include a brief comment at the top noting what changed (update the `Last analysed` date).

---

## 🏗️ Coding Conventions (Quick Reference)

When making changes to this project, follow the established patterns:

- **Package**: `com.ecommerce.{service-name}.{layer}`
- **Service pattern**: Interface + `serviceImpl/` sub-package implementation
- **DTOs**: `dto.request.*` and `dto.response.*` packages
- **Entities**: Extend `BaseEntity` for auditing, use UUID PKs, Lombok builders
- **New downstream services**: Use `HeaderAuthenticationFilter`, no Spring Security needed
- **New RBAC rules**: Add to `RouteValidator.hasRequiredRole()` in API Gateway
- **New Feign clients**: Include `FallbackFactory`, configure Resilience4j circuit breaker
- **Error handling**: Use common-exception classes, maintain standard error shape
