package com.reuniones.salareserva.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI / Swagger UI.
 *
 * <p>El esquema de seguridad JWT (Bearer) se añadirá junto con la seguridad en la tarea
 * correspondiente (Task 3). Aquí se define la metadata general de la API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI salaReservaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Reserva de Salas de Reuniones")
                        .description("API REST para crear y listar salas, y crear, listar y "
                                + "cancelar reservas. Los endpoints de consulta son públicos; "
                                + "crear y cancelar requieren autenticación JWT.")
                        .version("v1")
                        .contact(new Contact().name("Equipo sala-reserva"))
                        .license(new License().name("MIT")));
    }
}
