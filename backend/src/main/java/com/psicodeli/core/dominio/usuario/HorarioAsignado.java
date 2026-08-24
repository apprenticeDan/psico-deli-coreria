package com.psicodeli.core.dominio.usuario;

import java.time.LocalTime;
import java.util.Set;
import java.time.DayOfWeek;

public record HorarioAsignado(
    Set<DayOfWeek> dias,
    LocalTime horaInicio,
    LocalTime horaFin
) {
    public HorarioAsignado {
        if (dias == null || dias.isEmpty()) {
            throw new IllegalArgumentException("Debe asignar al menos un dia");
        }
        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("Debe asignar horas de inicio y fin");
        }
    }
}
