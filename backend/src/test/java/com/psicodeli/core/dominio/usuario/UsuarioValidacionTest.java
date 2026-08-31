package com.psicodeli.core.dominio.usuario;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioValidacionTest {

    @Test
    @DisplayName("crearTrabajador: Rechaza la creación si el nombre contiene números")
    void crearTrabajador_NombreConNumeros_RetornaError() {
        Result<Trabajador, ErrorDominio> res = UsuarioFunciones.crearTrabajador(
                "Carlos 123", "7654321", "71234567", "carlos123", "hashSecret123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(res.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, res.getError());
        assertEquals("nombreCompleto", ((ErrorDominio.FormatoInvalido) res.getError()).campo());
    }

    @Test
    @DisplayName("crearTrabajador: Rechaza la creación si el nombre contiene símbolos especiales")
    void crearTrabajador_NombreConSimbolosRaros_RetornaError() {
        Result<Trabajador, ErrorDominio> res = UsuarioFunciones.crearTrabajador(
                "Carlos @Admin", "7654321", "71234567", "carlos_admin", "hashSecret123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(res.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, res.getError());
    }

    @Test
    @DisplayName("crearTrabajador: Rechaza la creación si el teléfono contiene letras")
    void crearTrabajador_TelefonoConLetras_RetornaError() {
        Result<Trabajador, ErrorDominio> res = UsuarioFunciones.crearTrabajador(
                "Carlos Perez", "7654321", "71234567abc", "carlos_p", "hashSecret123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(res.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, res.getError());
        assertEquals("telefono", ((ErrorDominio.FormatoInvalido) res.getError()).campo());
    }

    @Test
    @DisplayName("crearTrabajador: Rechaza la creación si el usuario contiene espacios")
    void crearTrabajador_UsuarioConEspacios_RetornaError() {
        Result<Trabajador, ErrorDominio> res = UsuarioFunciones.crearTrabajador(
                "Carlos Perez", "7654321", "71234567", "carlos perez", "hashSecret123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(res.isError());
        assertInstanceOf(ErrorDominio.FormatoInvalido.class, res.getError());
        assertEquals("usuario", ((ErrorDominio.FormatoInvalido) res.getError()).campo());
    }

    @Test
    @DisplayName("crearTrabajador: Permite la creación con todos los campos válidos")
    void crearTrabajador_CamposValidos_RetornaOk() {
        Result<Trabajador, ErrorDominio> res = UsuarioFunciones.crearTrabajador(
                "Carlos Pérez", "7654321 LP", "71234567", "carlos.perez", "hashSecret123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(res.isOk());
        Trabajador t = res.getValue();
        assertEquals("Carlos Pérez", t.nombreCompleto());
        assertEquals("7654321 LP", t.cedulaIdentidad());
        assertEquals("71234567", t.telefono());
        assertEquals("carlos.perez", t.credencial().usuario());
    }
}
