package com.psicodeli.core.dominio.usuario;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record RegistroSesion(
    UUID id,
    UUID trabajadorId,
    LocalDateTime inicio,
    Optional<LocalDateTime> fin
) {
    public RegistroSesion {
        if (id == null || trabajadorId == null || inicio == null) {
            throw new IllegalArgumentException("ID, TrabajadorID e inicio no pueden ser nulos");
        }
    }
}
