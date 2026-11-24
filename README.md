# API REST — Reserva de Salas de Reuniones

API REST en **Java 17 + Spring Boot 3.3** para gestionar salas y reservas de reuniones, con
PostgreSQL + Flyway, autenticación JWT y documentación OpenAPI/Swagger.

## Funcionalidad

- Crear y listar **salas**.
- Crear, listar y cancelar **reservas** (cada reserva tiene sala, responsable, inicio y fin).
- Si la sala ya está ocupada en ese horario, la creación devuelve **409**. Datos inválidos → **400**
  con detalle por campo.
- Listado de reservas con filtro por **sala** y por **fecha**, con **paginación**.
- Los endpoints que **crean o cancelan** requieren **JWT**; los de **consulta** son públicos.

---

## Stack técnico

| Área | Tecnología |
|------|------------|
| Lenguaje / runtime | Java 17 (compila en JDK 21) |
| Framework | Spring Boot 3.3.5 (Web, Data JPA, Validation, Security) |
| Base de datos | PostgreSQL 16 + Flyway |
| Autenticación | JWT (jjwt 0.12) |
| Documentación | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Tests | JUnit 5, Mockito, MockMvc, Testcontainers |

**Arquitectura:** monolito modular. Paquete base `com.reuniones.salareserva` con los módulos
`auth`, `salas`, `reservas` y `shared` (configuración, manejo de errores, DTOs comunes).

---

## Requisitos previos

- **JDK 17+** (probado con JDK 21).
- **Maven 3.9+** (o el wrapper del sistema).
- **Docker** (para la base de datos y para los tests de integración).

---

## Cómo ejecutarlo

### 1. Levantar la base de datos

```bash
docker compose up -d db
```

Esto inicia PostgreSQL 16 en `127.0.0.1:5439` (base `sala_reserva`, usuario/clave `sala`/`sala`).
Flyway crea el esquema y los datos semilla al arrancar la aplicación.

### 2. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La API queda en `http://localhost:8080`.

### 3. Documentación interactiva (Swagger UI)

```
http://localhost:8080/swagger-ui.html
```

El botón **Authorize** permite pegar el token JWT (`Bearer`) para probar los endpoints protegidos.

### Usuario semilla

Para autenticarse desde el primer arranque:

| Usuario | Contraseña |
|---------|------------|
| `admin` | `admin123` |

---

## Variables de entorno

La configuración tiene valores por defecto para desarrollo local; se pueden sobrescribir:

| Variable | Por defecto | Descripción |
|----------|-------------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5439/sala_reserva` | URL JDBC |
| `DB_USER` / `DB_PASSWORD` | `sala` / `sala` | Credenciales de BD |
| `SERVER_PORT` | `8080` | Puerto HTTP |
| `JWT_SECRET` | *(valor de desarrollo)* | Secreto HMAC (mín. 32 bytes). **Cambiar en producción** |
| `JWT_EXPIRATION_MS` | `3600000` | Validez del token (1 h) |

---

## Endpoints

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/api/auth/register` | Público | Registra un usuario (rol `USER`) |
| POST | `/api/auth/login` | Público | Devuelve un token JWT |
| POST | `/api/salas` | **JWT** | Crea una sala |
| GET | `/api/salas` | Público | Lista las salas |
| POST | `/api/reservas` | **JWT** | Crea una reserva (409 si hay solape) |
| GET | `/api/reservas` | Público | Lista reservas con filtros y paginación |
| DELETE | `/api/reservas/{id}` | **JWT** | Cancela una reserva (borrado lógico) |

**Filtros de `GET /api/reservas`:** `salaId`, `fecha` (`YYYY-MM-DD`), `page`, `size`, `sort`.

### Ejemplos con `curl`

```bash
# 1) Login -> token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}' | jq -r .token)

# 2) Crear una sala (requiere JWT)
curl -X POST http://localhost:8080/api/salas \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"nombre":"Sala Norte","ubicacion":"Piso 1","capacidad":8}'

# 3) Crear una reserva (requiere JWT)
curl -X POST http://localhost:8080/api/reservas \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"salaId":1,"responsable":"Ana","inicio":"2026-12-01T10:00:00Z","fin":"2026-12-01T11:00:00Z"}'

# 4) Listar reservas (público) con filtros y paginación
curl "http://localhost:8080/api/reservas?salaId=1&fecha=2026-12-01&page=0&size=10"

# 5) Cancelar una reserva (requiere JWT)
curl -X DELETE http://localhost:8080/api/reservas/1 -H "Authorization: Bearer $TOKEN"
```

---

## Modelo de datos

- **usuarios** — `id`, `username` (único), `password` (hash BCrypt), `rol`, `created_at`.
- **salas** — `id`, `nombre` (único), `ubicacion`, `capacidad`, `activa`, `created_at`.
- **reservas** — `id`, `sala_id` (FK), `responsable`, `inicio`, `fin`, `estado`
  (`ACTIVA`/`CANCELADA`), `created_at`.

**Regla de solape:** dos reservas `ACTIVA` de la misma sala no pueden solaparse. Se garantiza en
dos niveles: validación en el servicio (respuesta 409 clara) y una *exclusion constraint* de
PostgreSQL (`EXCLUDE USING gist` sobre `tstzrange`) como red de seguridad ante concurrencia. El
intervalo es `[inicio, fin)`: una reserva que termina justo cuando otra empieza **no** se solapa.

---

## Tests

```bash
mvn test
```

- **Unitarios** (JUnit 5 + Mockito): lógica de reservas y del servicio JWT.
- **Integración** (`@SpringBootTest` + MockMvc): flujo completo sobre un **PostgreSQL real**
  levantado con **Testcontainers**, con las migraciones Flyway aplicadas.

> Los tests de integración requieren **Docker en ejecución**. El proyecto fija la versión de la
> API de Docker (`api.version=1.44` vía Surefire) para ser compatible con Docker Engine 29+.

---

## Decisiones tomadas

- **Arquitectura monolito modular.** Para el alcance del ejercicio, separar por módulos de negocio
  (`auth`, `salas`, `reservas`, `shared`) aporta límites claros y testabilidad sin el costo de una
  arquitectura hexagonal o de microservicios, que serían sobreingeniería aquí.
- **Control del solape en dos niveles.** La validación en el servicio devuelve un 409 claro en el
  flujo normal; la *exclusion constraint* en la base de datos garantiza la integridad incluso ante
  escrituras concurrentes. El uso de `tstzrange` con límite `[inicio, fin)` define sin ambigüedad
  que las reservas contiguas no se solapan.
- **Cancelación como borrado lógico** (`estado = CANCELADA`). Preserva el historial y las reservas
  canceladas dejan de bloquear el horario.
- **`responsable` como texto libre.** Es un dato de la reserva, independiente del usuario que la
  crea, lo que permite reservar en nombre de terceros.
- **Autenticación JWT stateless** con usuarios en base de datos y contraseñas con BCrypt. Las
  consultas (GET) son públicas; crear y cancelar requieren token. Se exponen `register` y `login`.
- **Esquema gobernado por Flyway** (`ddl-auto=validate`): la base de datos es la fuente de verdad
  del esquema y Hibernate solo valida el mapeo.
- **Errores consistentes** con `ProblemDetail` (RFC-7807) y un manejador centralizado; los errores
  de validación detallan el campo afectado.
- **Filtros dinámicos con JPA Specifications** y una respuesta de paginación propia
  (`PaginaResponse`) para no acoplar la API a la serialización interna de Spring Data.
- **Puerto 5439 para PostgreSQL**, para no colisionar con otras instancias de Postgres del entorno.

---

## Qué haría con más tiempo

- **Autorización por roles más fina** (p. ej. que solo un `ADMIN` pueda crear salas) y *refresh tokens*.
- **Más reglas de negocio**: impedir reservas en el pasado, duración máxima, horarios permitidos y  validar que la sala esté activa.
- **Zona horaria explícita** en el filtro por fecha (hoy se interpreta en UTC).
- **Mayor cobertura de pruebas**: tests de capa web aislada (`@WebMvcTest`), más casos borde y un escenario de concurrencia real contra la *exclusion constraint*.
- **Filtros y orden adicionales** (por responsable, estado o rango de fechas).
- **Observabilidad y operación**: Spring Boot Actuator, métricas y logs estructurados, un `Dockerfile` para la aplicación y un pipeline de CI (GitHub Actions).
- **Gestión de usuarios** (cambio de contraseña, bloqueo) y *rate limiting* en el login.

---

## Cómo usé la IA

Trabajé con **Claude (Claude Code)** como asistente de desarrollo, bajo una metodología de *Spec
Driven Development* y un flujo por tareas: cada tarea terminaba con un reporte y mi confirmación
antes de continuar; los *merge* a `develop` y los *push* los controlé yo.

**Qué le pedí.** Que primero generara los documentos de gobierno del proyecto (`CLAUDE.md`,
`PLAN.md`, `ESTADO.md`, `Memory.md`) y luego implementara la API en tareas incrementales
(bootstrap, esquema de BD, seguridad, salas, reservas, tests y documentación), siguiendo buenas
prácticas y sin inventar: ante dudas, debía consultarme.

**Qué acepté.** La propuesta de arquitectura modular, el modelo de datos con la *exclusion
constraint* para el solape, el diseño de seguridad JWT, el uso de JPA Specifications para los
filtros y la estrategia de pruebas (unitarias + integración con Testcontainers). También tomé como
válidas sus verificaciones de cada tarea (arranque real de la aplicación y pruebas con `curl`).

**Qué corregí o definí yo.** Tomé las decisiones de negocio cuando me consultó (incluir `register`,
`responsable` como texto libre, cancelación lógica). Le indiqué revisar los contenedores Docker
existentes y elegir un puerto libre (5439) en lugar del 5432 habitual. Mantuve bajo mi control la
estrategia de ramas (git-flow, `task-<n>-<nombre>`), los *merge* y los *push*. Revisé y validé cada
reporte antes de avanzar, incluida la resolución de un problema de compatibilidad entre
Testcontainers y Docker Engine 29 (ajuste de la versión de la API de Docker).
