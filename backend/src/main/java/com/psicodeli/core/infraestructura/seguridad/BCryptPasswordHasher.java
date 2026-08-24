package com.psicodeli.core.infraestructura.seguridad;

import com.psicodeli.core.aplicacion.usuario.puertos.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean verify(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
