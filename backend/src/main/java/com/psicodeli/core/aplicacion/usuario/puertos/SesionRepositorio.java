package com.psicodeli.core.aplicacion.usuario.puertos;

import com.psicodeli.core.dominio.usuario.RegistroSesion;
import java.util.List;
import java.util.UUID;

public interface SesionRepositorio {
    RegistroSesion guardar(RegistroSesion sesion);
    List<RegistroSesion> listarPorTrabajador(UUID trabajadorId);
}
