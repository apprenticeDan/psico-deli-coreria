package com.psicodeli.core.infraestructura.seguridad;

import com.psicodeli.core.aplicacion.usuario.puertos.JwtService;
import com.psicodeli.core.dominio.usuario.Trabajador;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    // Ideally, this should come from application.properties
    private static final String SECRET_KEY_STRING = "psicodeli-super-secret-key-for-jwt-signing-123456789";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    @Override
    public String generateToken(Trabajador trabajador) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", trabajador.rol().name());
        claims.put("id", trabajador.id().toString());
        claims.put("nombre", trabajador.nombreCompleto());
        
        return Jwts.builder()
                .claims(claims)
                .subject(trabajador.credencial().usuario())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return !extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
