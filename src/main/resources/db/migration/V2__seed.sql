-- =====================================================================
-- V2 — Datos semilla
-- =====================================================================

-- Usuario administrador inicial para poder autenticarse desde el primer
-- arranque. Credenciales: usuario "admin" / contraseña "admin123".
-- La contraseña está almacenada como hash BCrypt (coste 10), compatible
-- con el BCryptPasswordEncoder por defecto de Spring Security.
INSERT INTO usuarios (username, password, rol)
VALUES ('admin',
        '$2a$10$202Q8f9fH/mClRz6eGBAOeC5waZhiQMPODM.cBocmdFh23hEm51BS',
        'ADMIN');
