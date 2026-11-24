# ESTADO.md — Tablero de progreso

> Estado de cada tarea del proyecto. Se actualiza al cerrar cada tarea.
> Última actualización: 2026-11-24.

Leyenda: ⬜ Pendiente · 🟦 En progreso · ✅ Hecha (confirmada por el usuario)

| # | Tarea | Rama | Estado | Confirmada |
|---|-------|------|--------|------------|
| 0 | Spec/SDD (CLAUDE, PLAN, ESTADO, Memory) | `feature/analyse_project` | ✅ Hecha | Sí |
| 1 | Bootstrap del proyecto | `task-1-bootstrap` | ✅ Hecha | Sí (mergeado a develop) |
| 2 | Esquema BD + Flyway | `task-2-esquema-bd` | ✅ Hecha | Sí (mergeado a develop) |
| 3 | Seguridad JWT + Auth (login/register) | `task-3-seguridad-jwt` | ✅ Hecha | Sí (mergeado a develop) |
| 4 | Módulo Salas (crear/listar) | `task-4-salas` | ✅ Hecha | Sí (mergeado a develop) |
| 5 | Módulo Reservas (crear/cancelar/listar) | `task-5-reservas` | ✅ Hecha | Sí (mergeado a develop) |
| 6 | Tests (≥3, incl. integración MockMvc) | `task-6-tests` | ✅ Hecha | Pendiente |
| 7 | README + documentación | `task-7-readme` | ⬜ Pendiente | — |

## Bitácora
- **2026-11-24** — Inicio del proyecto. Lectura de `promt_init.txt`, confirmación de decisiones
  de diseño (arquitectura modular, auth con login+register, responsable como texto libre,
  cancelación lógica). Creación de archivos de gobierno SDD (Task 0). **Task 0 aprobada.**
- **2026-11-24** — Task 1 (bootstrap): proyecto Maven + Spring Boot 3.3.5, `pom.xml`,
  `application.yml`, `docker-compose.yml` (Postgres en puerto host **5439**), `OpenApiConfig`.
  Verificado: compila, la app arranca contra Postgres, Swagger UI responde (200) y Flyway
  conecta. Seguridad/JWT diferido a Task 3; Testcontainers a Task 6. **Mergeado a develop (pusheado).**
- **2026-11-24** — Task 2 (esquema BD): migraciones Flyway `V1__init.sql` (tablas `usuarios`,
  `salas`, `reservas` con checks, FK, exclusion constraint de solape + `btree_gist`, índice) y
  `V2__seed.sql` (usuario `admin`). Verificado contra Postgres: migraciones aplican, solape
  bloqueado, reserva contigua permitida, rango inválido rechazado. **Mergeado a develop (pusheado).**
- **2026-11-24** — Task 3 (seguridad JWT): módulo `auth` (entidad `Usuario`, repo,
  `JwtService`, `JwtAuthenticationFilter`, `UsuarioDetailsService`, `AuthService`,
  `AuthController` con `/register` y `/login`, DTOs), `SecurityConfig` (stateless, rutas
  públicas vs protegidas, 401/403 JSON), `GlobalExceptionHandler` (400/401/409) y esquema
  Bearer en Swagger. Deps añadidas: spring-boot-starter-security, jjwt 0.12.6. Verificado:
  login admin 200+token, register 201, duplicado 409, credenciales malas 401, validación 400,
  ruta protegida sin token 401 / con token 404 / token inválido 401, GET público no bloqueado.
  **Mergeado a develop (pusheado).**
- **2026-11-24** — Task 4 (módulo salas): entidad `Sala`, `SalaRepository`, `SalaService`,
  `SalaController` (`POST /api/salas` protegido, `GET /api/salas` público), DTOs
  (`CrearSalaRequest`, `SalaResponse`) y excepción `SalaYaExisteException` (409). Verificado:
  POST sin token 401 / con token 201, duplicado 409, validación 400, GET público 200 ordenado
  por nombre. **Mergeado a develop (pusheado).**
- **2026-11-24** — Task 5 (módulo reservas): entidad `Reserva` + enum `EstadoReserva`,
  `ReservaRepository` (`existeSolape`, `JpaSpecificationExecutor`), `ReservaSpecifications`
  (filtros sala/fecha), `ReservaService`, `ReservaController` (`POST` y `DELETE` protegidos,
  `GET` público paginado), DTOs (`CrearReservaRequest`, `ReservaResponse`,
  `shared/dto/PaginaResponse`) y excepciones `RecursoNoEncontradoException` (404),
  `SolapeReservaException` (409), `ReglaNegocioException` (400). Verificado: crear 201,
  sin token 401, solape 409, contigua 201, otra sala 201, fin<inicio 400, sala inexistente 404,
  validación 400; listado con filtros sala/fecha y paginación; cancelar 204 (lógico, libera
  horario) / sin token 401 / inexistente 404; cancelada visible como CANCELADA.
  **Mergeado a develop (pusheado).**
- **2026-11-24** — Task 6 (tests): 14 tests en verde. `JwtServiceTest` (4) y `ReservaServiceTest`
  (6, Mockito) unitarios; `ReservaIntegrationTest` (4, **MockMvc + Testcontainers Postgres**)
  cubre el flujo completo (login, crear sala, crear reserva, solape 409, listar, cancelar y
  re-reservar), 401 sin token, GET público y 400 de validación. Fix de compatibilidad Docker 29:
  `api.version=1.44` vía Surefire (docker-java por defecto pedía API 1.32, rechazada por el
  daemon). **Commit local pendiente de push.**
