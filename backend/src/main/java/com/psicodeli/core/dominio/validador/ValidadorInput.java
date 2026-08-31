package com.psicodeli.core.dominio.validador;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;

import java.util.regex.Pattern;

public class ValidadorInput {

    private static final Pattern PATRON_NOMBRE = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑäëïöüÄËÏÖÜ\\s'-]{2,100}$");
    private static final Pattern PATRON_USUARIO = Pattern.compile("^[a-zA-Z0-9._]{3,30}$");
    private static final Pattern PATRON_CI = Pattern.compile("^[a-zA-Z0-9-]{5,15}(\\s[a-zA-Z]{2,3})?$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\+?[0-9]{7,15}$");

    private ValidadorInput() {}

    public static Result<String, ErrorDominio> validarNombrePersona(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("El nombre no puede estar vacío"));
        }
        String nombreLimpio = nombre.trim();
        if (!PATRON_NOMBRE.matcher(nombreLimpio).matches()) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "nombreCompleto", 
                "El nombre solo debe contener letras, espacios, guiones o apóstrofes, sin números ni símbolos especiales"
            ));
        }
        return Result.ok(nombreLimpio);
    }

    public static Result<String, ErrorDominio> validarNombreUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("El usuario no puede estar vacío"));
        }
        String usuarioLimpio = usuario.trim();
        if (!PATRON_USUARIO.matcher(usuarioLimpio).matches()) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "usuario", 
                "El usuario debe contener entre 3 y 30 caracteres alfanuméricos, puntos o guiones bajos, sin espacios ni símbolos especiales"
            ));
        }
        return Result.ok(usuarioLimpio);
    }

    public static Result<String, ErrorDominio> validarPassword(String password) {
        if (password == null || password.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("La contraseña no puede estar vacía"));
        }
        if (password.length() < 8) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "password", 
                "La contraseña debe tener al menos 8 caracteres"
            ));
        }
        if (password.contains("\0")) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "password", 
                "La contraseña contiene caracteres no permitidos"
            ));
        }
        // Regla de fortaleza: Al menos una mayúscula, una minúscula, un número y un carácter especial
        // Expresión regular: (?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.#_-])
        String regexFortaleza = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_-]).+$";
        if (!password.matches(regexFortaleza)) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "password",
                "La contraseña debe contener al menos una letra mayúscula, una minúscula, un número y un carácter especial (@$!%*?&.#_-)"
            ));
        }

        return Result.ok(password);
    }

    public static Result<String, ErrorDominio> validarCedulaIdentidad(String ci) {
        if (ci == null || ci.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("La cédula de identidad no puede estar vacía"));
        }
        String ciLimpio = ci.trim();
        /*if (!PATRON_CI.matcher(ciLimpio).matches()) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "cedulaIdentidad", 
                "La cédula de identidad no tiene un formato válido (5 a 15 caracteres alfanuméricos con extensión opcional)"
            ));
        }*/
        return Result.ok(ciLimpio);
    }

    public static Result<String, ErrorDominio> validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("El teléfono no puede estar vacío"));
        }
        String telefonoLimpio = telefono.trim();
        if (!PATRON_TELEFONO.matcher(telefonoLimpio).matches()) {
            return Result.error(new ErrorDominio.FormatoInvalido(
                "telefono", 
                "El campo teléfono debe ser exclusivamente numérico (entre 7 y 15 dígitos) y no puede contener letras ni símbolos"
            ));
        }
        return Result.ok(telefonoLimpio);
    }
}
