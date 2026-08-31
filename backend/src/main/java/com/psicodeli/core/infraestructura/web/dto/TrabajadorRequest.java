package com.psicodeli.core.infraestructura.web.dto;

import com.psicodeli.core.dominio.usuario.Rol;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record TrabajadorRequest(
    String nombreCompleto,
    String cedulaIdentidad,
    String telefono,
    String usuario,
    String password,
    Rol rol,
    Set<DayOfWeek> horarioDias,
    LocalTime horarioHoraInicio,
    LocalTime horarioHoraFin
) {}
