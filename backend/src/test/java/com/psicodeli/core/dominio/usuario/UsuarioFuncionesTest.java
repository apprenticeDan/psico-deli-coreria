package com.psicodeli.core.dominio.usuario;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioFuncionesTest {

    private final BiFunction<String, String, Boolean> mockHasher = String::equals;

    private Trabajador crearTrabajadorMock(String usuario, EstadoTrabajador estado) {
        return new Trabajador(
                UUID.randomUUID(),
                "Juan Perez",
                new Credencial(usuario, "hash123456"),
                Rol.VENDEDOR,
                estado,
                Optional.empty()
        );
    }

    @Test
    void autenticar_CredencialesValidas_RetornaOk() {
        Trabajador trabajador = crearTrabajadorMock("juanp", EstadoTrabajador.ACTIVO);
        List<Trabajador> lista = List.of(trabajador);

        Result<Trabajador, ErrorDominio> result = UsuarioFunciones.autenticar(
                "juanp", "hash123456", lista, mockHasher
        );

        assertTrue(result.isOk());
        assertEquals(trabajador, result.getValue());
    }

    @Test
    void autenticar_CredencialesInvalidas_RetornaError() {
        Trabajador trabajador = crearTrabajadorMock("juanp", EstadoTrabajador.ACTIVO);
        List<Trabajador> lista = List.of(trabajador);

        Result<Trabajador, ErrorDominio> result = UsuarioFunciones.autenticar(
                "juanp", "wrongpass123", lista, mockHasher
        );

        assertTrue(result.isError());
        assertInstanceOf(ErrorDominio.CredencialesInvalidas.class, result.getError());
    }

    @Test
    void autenticar_TrabajadorInactivo_RetornaError() {
        Trabajador trabajador = crearTrabajadorMock("juanp", EstadoTrabajador.INACTIVO);
        List<Trabajador> lista = List.of(trabajador);

        Result<Trabajador, ErrorDominio> result = UsuarioFunciones.autenticar(
                "juanp", "hash123456", lista, mockHasher
        );

        assertTrue(result.isError());
        assertInstanceOf(ErrorDominio.TrabajadorInactivo.class, result.getError());
    }

    @Test
    void puedeEjecutar_Administrador_RetornaTrue() {
        assertTrue(UsuarioFunciones.puedeEjecutar(Rol.ADMINISTRADOR, "CUALQUIER_ACCION"));
    }

    @Test
    void puedeEjecutar_VendedorAccionPermitida_RetornaTrue() {
        assertTrue(UsuarioFunciones.puedeEjecutar(Rol.VENDEDOR, "REGISTRAR_VENTA"));
    }

    @Test
    void puedeEjecutar_VendedorAccionNoPermitida_RetornaFalse() {
        assertFalse(UsuarioFunciones.puedeEjecutar(Rol.VENDEDOR, "GESTIONAR_TRABAJADORES"));
    }

    @Test
    void crearTrabajador_DatosValidos_RetornaOk() {
        Result<Trabajador, ErrorDominio> result = UsuarioFunciones.crearTrabajador(
                "Maria Lopez", "marial", "hash456789", Rol.ADMINISTRADOR, Optional.empty()
        );

        assertTrue(result.isOk());
        Trabajador t = result.getValue();
        assertEquals("Maria Lopez", t.nombreCompleto());
        assertEquals("marial", t.credencial().usuario());
        assertEquals(Rol.ADMINISTRADOR, t.rol());
        assertEquals(EstadoTrabajador.ACTIVO, t.estado());
    }
}
