package com.psicodeli.core.dominio.validador;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorInputTest {

    @Test
    @DisplayName("validarNombrePersona: Acepta nombres válidos con letras, espacios, tildes y apóstrofes")
    void validarNombrePersona_NombreValido_RetornaOk() {
        Result<String, ErrorDominio> r1 = ValidadorInput.validarNombrePersona("Juan Pérez");
        Result<String, ErrorDominio> r2 = ValidadorInput.validarNombrePersona("Ana-María D'Amico");
        Result<String, ErrorDominio> r3 = ValidadorInput.validarNombrePersona("René Ñancahuasú");

        assertTrue(r1.isOk());
        assertEquals("Juan Pérez", r1.getValue());

        assertTrue(r2.isOk());
        assertEquals("Ana-María D'Amico", r2.getValue());

        assertTrue(r3.isOk());
        assertEquals("René Ñancahuasú", r3.getValue());
    }

    @Test
    @DisplayName("validarNombrePersona: Rechaza nombres que contienen números")
    void validarNombrePersona_ConNumeros_RetornaErrorFormatoInvalido() {
        Result<String, ErrorDominio> r1 = ValidadorInput.validarNombrePersona("Juan123");
        Result<String, ErrorDominio> r2 = ValidadorInput.validarNombrePersona("Ana 2026");

        assertTrue(r1.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, r1.getError());

        assertTrue(r2.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, r2.getError());
    }

    @Test
    @DisplayName("validarNombrePersona: Rechaza nombres con símbolos especiales o código malicioso")
    void validarNombrePersona_ConSimbolos_RetornaErrorFormatoInvalido() {
        Result<String, ErrorDominio> r1 = ValidadorInput.validarNombrePersona("Carlos@admin");
        Result<String, ErrorDominio> r2 = ValidadorInput.validarNombrePersona("<script>alert(1)</script>");
        Result<String, ErrorDominio> r3 = ValidadorInput.validarNombrePersona("Pedro % $ #");

        assertTrue(r1.isError());
        assertTrue(r2.isError());
        assertTrue(r3.isError());
    }

    @Test
    @DisplayName("validarNombrePersona: Rechaza entradas nulas o en blanco")
    void validarNombrePersona_Vacio_RetornaErrorValorInvalido() {
        Result<String, ErrorDominio> r1 = ValidadorInput.validarNombrePersona("");
        Result<String, ErrorDominio> r2 = ValidadorInput.validarNombrePersona("   ");
        Result<String, ErrorDominio> r3 = ValidadorInput.validarNombrePersona(null);

        assertTrue(r1.isError());
        assertTrue(r2.isError());
        assertTrue(r3.isError());
    }

    @Test
    @DisplayName("validarNombreUsuario: Acepta nombres de usuario alfanuméricos entre 3 y 30 caracteres con punto y guion bajo")
    void validarNombreUsuario_UsuarioValido_RetornaOk() {
        Result<String, ErrorDominio> r1 = ValidadorInput.validarNombreUsuario("juan_perez");
        Result<String, ErrorDominio> r2 = ValidadorInput.validarNombreUsuario("admin.01");

        assertTrue(r1.isOk());
        assertEquals("juan_perez", r1.getValue());

        assertTrue(r2.isOk());
        assertEquals("admin.01", r2.getValue());
    }

    @Test
    @DisplayName("validarNombreUsuario: Rechaza usuarios con espacios o símbolos raros")
    void validarNombreUsuario_ConEspaciosOSimbolos_RetornaError() {
        Result<String, ErrorDominio> r1 = ValidadorInput.validarNombreUsuario("juan perez");
        Result<String, ErrorDominio> r2 = ValidadorInput.validarNombreUsuario("user#1");
        Result<String, ErrorDominio> r3 = ValidadorInput.validarNombreUsuario("admin$");

        assertTrue(r1.isError());
        assertTrue(r2.isError());
        assertTrue(r3.isError());
    }

    @Test
    @DisplayName("validarNombreUsuario: Rechaza nombres de usuario demasiado cortos (< 3 caracteres)")
    void validarNombreUsuario_DemasiadoCorto_RetornaError() {
        Result<String, ErrorDominio> r = ValidadorInput.validarNombreUsuario("ab");
        assertTrue(r.isError());
    }

    @Test
    @DisplayName("validarPassword: Exige al menos 8 caracteres y rechaza nulos/vacíos")
    void validarPassword_ReglasBasicas_FuncionaCorrectamente() {
        Result<String, ErrorDominio> ok = ValidadorInput.validarPassword("Password123");
        Result<String, ErrorDominio> corta = ValidadorInput.validarPassword("pass");
        Result<String, ErrorDominio> vacia = ValidadorInput.validarPassword("");

        assertTrue(ok.isOk());
        assertTrue(corta.isError());
        assertTrue(vacia.isError());
    }

    @Test
    @DisplayName("validarCedulaIdentidad: Acepta CIs válidas y rechaza símbolos extraños")
    void validarCedulaIdentidad_Validaciones_FuncionaCorrectamente() {
        Result<String, ErrorDominio> ok1 = ValidadorInput.validarCedulaIdentidad("1234567");
        Result<String, ErrorDominio> ok2 = ValidadorInput.validarCedulaIdentidad("1234567 LP");
        Result<String, ErrorDominio> err = ValidadorInput.validarCedulaIdentidad("1234@56");

        assertTrue(ok1.isOk());
        assertTrue(ok2.isOk());
        assertTrue(err.isError());
    }

    @Test
    @DisplayName("validarTelefono: Exige formato exclusivamente numérico (7 a 15 dígitos) y rechaza letras/símbolos")
    void validarTelefono_ExclusivamenteNumerico_RechazaLetrasYSimbolos() {
        Result<String, ErrorDominio> okLocal = ValidadorInput.validarTelefono("71234567");
        Result<String, ErrorDominio> okIntl = ValidadorInput.validarTelefono("+59171234567");
        Result<String, ErrorDominio> errLetras = ValidadorInput.validarTelefono("71234567a");
        Result<String, ErrorDominio> errSimbolos = ValidadorInput.validarTelefono("7123-4567#");
        Result<String, ErrorDominio> errCorto = ValidadorInput.validarTelefono("12345");

        assertTrue(okLocal.isOk());
        assertEquals("71234567", okLocal.getValue());

        assertTrue(okIntl.isOk());
        assertEquals("+59171234567", okIntl.getValue());

        assertTrue(errLetras.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, errLetras.getError());

        assertTrue(errSimbolos.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, errSimbolos.getError());

        assertTrue(errCorto.isError());
    }
}
