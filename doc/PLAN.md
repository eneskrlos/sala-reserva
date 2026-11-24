# PLAN.md — API REST Reserva de Salas de Reuniones

> Especificación y plan de ejecución (Spec Driven Development).
> Última actualización: 2026-11-24.

## Objetivo
API REST (Java 17 + Spring Boot) para reservar salas de reuniones, con PostgreSQL + Flyway,
autenticación JWT, documentación Swagger, tests (incl. integración MockMvc) y README.

## Requisitos funcionales (del enunciado)
1. Crear y listar salas. Crear, listar y cancelar reservas. Cada reserva tiene sala,
   responsable, inicio y fin.
2. Si la sala ya está ocupada en ese horario → **409**. Validación de datos con **400** claros.
3. Listado de reservas con filtro por **sala** y por **fecha**, con **paginación**.
4. **PostgreSQL** con migraciones (**Flyway**).
5. Endpoints que **crean o cancelan** requieren **JWT**; los de **consulta no**.
6. Al menos **3 tests**, uno de ellos de **integración (MockMvc)**.
7. **README** con: cómo ejecutarlo.

## Requisitos no funcionales
- Arquitectura **Monolito Modular**. Buenas prácticas y calidad de código.
- Documentación con **Swagger/OpenAPI**.
- Estructura de base de datos derivada del dominio (ver `Memory.md`).

## Modelo de datos (resumen; detalle en `Memory.md`)
- `usuarios(id, username*, password, rol, created_at)`
- `salas(id, nombre*, ubicacion, capacidad, activa, created_at)`
- `reservas(id, sala_id→salas, responsable, inicio, fin, estado, created_at)`
  - `CHECK (fin > inicio)`; solape bloqueado en servicio (409) y por *exclusion constraint* en BD.

## Endpoints
| Método | Ruta | Auth | Resultado |
|---|---|---|---|
| POST | `/api/auth/register` | público | 201 crea usuario |
| POST | `/api/auth/login` | público | 200 + JWT |
| POST | `/api/salas` | JWT | 201 / 400 |
| GET | `/api/salas` | público | 200 lista |
| POST | `/api/reservas` | JWT | 201 / 400 / 409 |
| GET | `/api/reservas` | público | 200 paginado + filtros `salaId`, `fecha` |
| DELETE | `/api/reservas/{id}` | JWT | 204 (borrado lógico → CANCELADA) |

## Secuencia de tareas
Cada tarea se desarrolla en su rama `task-<n>-<nombre>` (desde `develop`), termina con un
**reporte** y **espera confirmación** antes de continuar.

- **Task 0 — Spec/SDD** *(rama `feature/analyse_project`)*: crear `CLAUDE.md`, `PLAN.md`,
  `ESTADO.md`, `Memory.md`.
- **Task 1 — `task-1-bootstrap`**: estructura Maven, `pom.xml`, `application.yml`,
  `docker-compose.yml` (Postgres), clase main, `OpenApiConfig`.
- **Task 2 — `task-2-esquema-bd`**: Flyway `V1__init.sql` (tablas + constraints/solape),
  `V2__seed.sql` (usuario semilla).
- **Task 3 — `task-3-seguridad-jwt`**: módulo `auth` (register, login), `JwtService`,
  `JwtAuthFilter`, `SecurityConfig`.
- **Task 4 — `task-4-salas`**: módulo `salas` (crear + listar).
- **Task 5 — `task-5-reservas`**: módulo `reservas` (crear con 409, cancelar lógico, listar con
  filtros + paginación, validaciones 400).
- **Task 6 — `task-6-tests`**: ≥3 tests, incl. integración MockMvc.
- **Task 7 — `task-7-readme`**: README completo (req. 7).

## Verificación end-to-end
```bash
docker compose up -d db
mvn clean verify
mvn spring-boot:run
# Swagger UI: http://localhost:8080/swagger-ui.html
```
Flujo: register → login (token) → crear sala → crear reserva (201) → reserva solapada (409) →
listar con filtros/paginación → cancelar (204) → la cancelada aparece como `CANCELADA`.
