package com.psicodeli.core.aplicacion.usuario.puertos;

import com.psicodeli.core.dominio.usuario.Trabajador;

public interface JwtService {
    String generateToken(Trabajador trabajador);
    boolean validateToken(String token);
    String extractUsername(String token);
}
