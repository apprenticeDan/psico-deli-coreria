package com.psicodeli.core.infraestructura.web.dto;

import com.psicodeli.core.dominio.usuario.EstadoTrabajador;
import com.psicodeli.core.dominio.usuario.Rol;
import java.util.UUID;

public record TrabajadorResponse(
    UUID id,
    String nombreCompleto,
    String cedulaIdentidad,
    String telefono,
    String usuario,
    Rol rol,
    EstadoTrabajador estado
) {}
