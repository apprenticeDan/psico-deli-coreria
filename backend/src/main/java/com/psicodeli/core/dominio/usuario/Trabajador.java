package com.psicodeli.core.dominio.usuario;

import java.util.Optional;
import java.util.UUID;

public record Trabajador(
    UUID id,
    String nombreCompleto,
    Credencial credencial,
    Rol rol,
    EstadoTrabajador estado,
    Optional<HorarioAsignado> horarioAsignado
) {
    public Trabajador {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("Nombre no puede ser vacio");
        }
        if (credencial == null) {
            throw new IllegalArgumentException("Credencial no puede ser nula");
        }
        if (rol == null) {
            throw new IllegalArgumentException("Rol no puede ser nulo");
        }
        if (estado == null) {
            throw new IllegalArgumentException("Estado no puede ser nulo");
        }
    }
}
