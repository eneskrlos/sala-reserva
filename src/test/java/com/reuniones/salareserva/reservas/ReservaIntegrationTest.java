package com.reuniones.salareserva.reservas;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Prueba de integración de punta a punta (MockMvc) sobre un PostgreSQL real (Testcontainers),
 * con las migraciones Flyway aplicadas y el usuario semilla "admin".
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ReservaIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /** Inicia sesión como admin (usuario semilla) y devuelve el header Authorization. */
    private String tokenAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin123"}"""))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return "Bearer " + body.get("token").asText();
    }

    private long crearSala(String auth, String nombre) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/salas")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String reservaJson(long salaId, String responsable, String inicio, String fin) {
        return """
                {"salaId":%d,"responsable":"%s","inicio":"%s","fin":"%s"}"""
                .formatted(salaId, responsable, inicio, fin);
    }

    @Test
    @DisplayName("Flujo completo: crear sala, crear reserva, solape 409, listar y cancelar")
    void flujoCompleto() throws Exception {
        String auth = tokenAdmin();
        long salaId = crearSala(auth, "Integración Flujo");

        // Crear reserva -> 201
        MvcResult creada = mockMvc.perform(post("/api/reservas")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, "Ana",
                                "2026-12-01T10:00:00Z", "2026-12-01T11:00:00Z")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andReturn();
        long reservaId = objectMapper.readTree(creada.getResponse().getContentAsString())
                .get("id").asLong();

        // Reserva solapada misma sala -> 409
        mockMvc.perform(post("/api/reservas")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, "Beto",
                                "2026-12-01T10:30:00Z", "2026-12-01T11:30:00Z")))
                .andExpect(status().isConflict());

        // Listar (público) filtrando por sala -> contiene al menos 1
        mockMvc.perform(get("/api/reservas").param("salaId", String.valueOf(salaId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.contenido[0].salaId").value((int) salaId));

        // Cancelar (borrado lógico) -> 204
        mockMvc.perform(delete("/api/reservas/{id}", reservaId)
                        .header("Authorization", auth))
                .andExpect(status().isNoContent());

        // Tras cancelar, el mismo horario queda libre -> 201
        mockMvc.perform(post("/api/reservas")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson(salaId, "Caro",
                                "2026-12-01T10:00:00Z", "2026-12-01T11:00:00Z")))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Crear sala sin token devuelve 401")
    void crearSalaSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/salas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"No autorizada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Listar reservas es público (200 sin token)")
    void listarReservasEsPublico() throws Exception {
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").isArray());
    }

    @Test
    @DisplayName("Crear reserva con datos inválidos devuelve 400")
    void crearReservaInvalidaDevuelve400() throws Exception {
        String auth = tokenAdmin();
        mockMvc.perform(post("/api/reservas")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"responsable\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").exists());
    }
}
