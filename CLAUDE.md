# CLAUDE.md — Guía del proyecto para Claude

Este archivo orienta a Claude (y a cualquier IA/colaborador) sobre **cómo trabajar** en este
repositorio. Es la fuente de verdad de convenciones y reglas de equipo.

## Qué es este proyecto
API REST en **Java 17 + Spring Boot** para **reservar salas de reuniones**.
Metodología: **Spec Driven Development (SDD)** — primero la especificación (`PLAN.md`,
`Memory.md`), luego la implementación tarea por tarea, con el estado reflejado en `ESTADO.md`.

## Rol de Claude
Senior backend Java. Conoce: Java 17, Spring Boot, PostgreSQL, Flyway, JWT, Swagger/OpenAPI,
tests con MockMvc. Debe aplicar **buenas prácticas y calidad de software** en todo momento.

## Reglas de trabajo en equipo (OBLIGATORIAS)
1. **No inventar.** Ante cualquier duda, preguntar **antes** de actuar.
2. **Entrega por tareas.** Al terminar cada tarea, entregar un **reporte** y **esperar
   confirmación** del usuario antes de pasar a la siguiente. El reporte incluye:
   - Nombre de la tarea
   - Descripción
   - Archivos creados o modificados
   - Patrones de diseño utilizados
3. **Los push los hace el usuario.** Claude puede hacer *commits locales* si se le pide,
   pero **nunca `git push`**.
4. **Ramas nuevas**: formato `task-<numero>-<nombreTarea>` (ej.: `task-1-bootstrap`),
   creadas desde `develop` (flujo git-flow). La rama de análisis/spec es `feature/analyse_project`.

## Stack técnico
- Java **17** (JDK 21 en la máquina; se compila con `maven.compiler.release=17`).
- Spring Boot **3.3.x**, **Maven** (no Gradle).
- Spring Web, Spring Data JPA, Bean Validation, Spring Security.
- **PostgreSQL** + **Flyway** (migraciones versionadas en `src/main/resources/db/migration`).
- **JWT** con `io.jsonwebtoken` (jjwt).
- **Swagger/OpenAPI**: `springdoc-openapi-starter-webmvc-ui` (`/swagger-ui.html`).
- Tests: JUnit 5, MockMvc, Testcontainers (PostgreSQL) para integración.
- **Docker** disponible (Compose para Postgres local).

## Arquitectura
**Monolito Modular**. Paquete base `com.reuniones.salareserva`, módulos: `auth`, `salas`,
`reservas`, `shared`. Cada módulo encapsula su controller/service/repository/entidades/DTOs.

## Convenciones
- **Idioma**: código/identificadores del dominio en español (coherente con el enunciado);
  comentarios y docs en español.
- **DTOs** para entrada/salida; nunca exponer entidades JPA directamente.
- **Errores** centralizados con `@RestControllerAdvice` y `ProblemDetail` (RFC-7807).
  400 (validación), 401/403 (auth), 404 (no encontrado), 409 (solape de reserva).
- **Commits**: mensajes claros en español, con prefijo del tipo (`feat:`, `fix:`, `docs:`,
  `test:`, `chore:`). Al final del mensaje de commit, agregar:
  `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`
- **Migraciones Flyway** inmutables una vez aplicadas; cambios → nueva versión `V<n>__*.sql`.

## Decisiones de negocio confirmadas
- `responsable` de una reserva = **texto libre** (nombre/email) enviado en el body.
- **Cancelar** una reserva = **borrado lógico** (`estado=CANCELADA`); las canceladas **no**
  bloquean el horario.
- **Solape (409)**: en la misma sala, dos reservas `ACTIVA` chocan si
  `inicio < fin_existente AND fin > inicio_existente`.
- **Auth**: tabla `usuarios` con semilla (password BCrypt) + endpoints `login` **y** `register`.
  Endpoints de **consulta (GET) públicos**; los que **crean o cancelan requieren JWT**.

## Archivos de gobierno (SDD)
- `PLAN.md` — plan y secuencia de tareas.
- `ESTADO.md` — tablero de progreso (estado de cada tarea).
- `Memory.md` — memoria del proyecto: decisiones, modelo de datos, aprendizajes.
- `CLAUDE.md` — este archivo (convenciones y reglas).
