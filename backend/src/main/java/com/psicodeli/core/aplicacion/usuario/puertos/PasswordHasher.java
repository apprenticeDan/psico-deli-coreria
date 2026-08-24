package com.psicodeli.core.aplicacion.usuario.puertos;

public interface PasswordHasher {
    String hash(String plainPassword);
    boolean verify(String plainPassword, String hashedPassword);
}
