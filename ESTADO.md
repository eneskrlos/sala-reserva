# ESTADO.md — Tablero de progreso

> Estado de cada tarea del proyecto. Se actualiza al cerrar cada tarea.
> Última actualización: 2026-11-24.

Leyenda: ⬜ Pendiente · 🟦 En progreso · ✅ Hecha (confirmada por el usuario)

| # | Tarea | Rama | Estado | Confirmada |
|---|-------|------|--------|------------|
| 0 | Spec/SDD (CLAUDE, PLAN, ESTADO, Memory) | `feature/analyse_project` | ✅ Hecha | Sí |
| 1 | Bootstrap del proyecto | `task-1-bootstrap` | ✅ Hecha | Pendiente |
| 2 | Esquema BD + Flyway | `task-2-esquema-bd` | ⬜ Pendiente | — |
| 3 | Seguridad JWT + Auth (login/register) | `task-3-seguridad-jwt` | ⬜ Pendiente | — |
| 4 | Módulo Salas (crear/listar) | `task-4-salas` | ⬜ Pendiente | — |
| 5 | Módulo Reservas (crear/cancelar/listar) | `task-5-reservas` | ⬜ Pendiente | — |
| 6 | Tests (≥3, incl. integración MockMvc) | `task-6-tests` | ⬜ Pendiente | — |
| 7 | README + documentación | `task-7-readme` | ⬜ Pendiente | — |

## Bitácora
- **2026-11-24** — Inicio del proyecto. Lectura de `promt_init.txt`, confirmación de decisiones
  de diseño (arquitectura modular, auth con login+register, responsable como texto libre,
  cancelación lógica). Creación de archivos de gobierno SDD (Task 0). **Task 0 aprobada.**
- **2026-11-24** — Task 1 (bootstrap): proyecto Maven + Spring Boot 3.3.5, `pom.xml`,
  `application.yml`, `docker-compose.yml` (Postgres en puerto host **5439**), `OpenApiConfig`.
  Verificado: compila, la app arranca contra Postgres, Swagger UI responde (200) y Flyway
  conecta. Seguridad/JWT diferido a Task 3; Testcontainers a Task 6. **Commit local pendiente de push.**
