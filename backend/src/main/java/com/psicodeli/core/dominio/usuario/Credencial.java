package com.psicodeli.core.dominio.usuario;

public record Credencial(String usuario, String passwordHash) {
    public Credencial {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Usuario no puede ser vacio");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash no puede ser vacio");
        }
    }
}
