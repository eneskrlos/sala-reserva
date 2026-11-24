package com.reuniones.salareserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API de reserva de salas de reuniones.
 *
 * <p>Arquitectura: Monolito Modular. Los módulos de negocio viven bajo este paquete base
 * ({@code auth}, {@code salas}, {@code reservas}) junto a utilidades compartidas ({@code shared}).
 */
@SpringBootApplication
public class SalaReservaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalaReservaApplication.class, args);
    }
}
