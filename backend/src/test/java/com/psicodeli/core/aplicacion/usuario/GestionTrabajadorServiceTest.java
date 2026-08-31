package com.psicodeli.core.aplicacion.usuario;

import com.psicodeli.core.aplicacion.usuario.puertos.PasswordHasher;
import com.psicodeli.core.aplicacion.usuario.puertos.TrabajadorRepositorio;
import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.usuario.Credencial;
import com.psicodeli.core.dominio.usuario.EstadoTrabajador;
import com.psicodeli.core.dominio.usuario.Rol;
import com.psicodeli.core.dominio.usuario.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GestionTrabajadorServiceTest {

    private TrabajadorRepositorio mockRepositorio;
    private PasswordHasher mockHasher;
    private GestionTrabajadorService service;
    private List<Trabajador> bdFalsa;

    @BeforeEach
    void setUp() {
        bdFalsa = new ArrayList<>();
        mockHasher = new PasswordHasher() {
            @Override
            public String hash(String plainPassword) {
                return "hashed_" + plainPassword;
            }

            @Override
            public boolean verify(String plainPassword, String hashedPassword) {
                return ("hashed_" + plainPassword).equals(hashedPassword);
            }
        };

        mockRepositorio = new TrabajadorRepositorio() {
            @Override
            public Optional<Trabajador> buscarPorUsuario(String usuario) {
                return bdFalsa.stream().filter(t -> t.credencial().usuario().equals(usuario)).findFirst();
            }

            @Override
            public Optional<Trabajador> buscarPorCedulaIdentidad(String ci) {
                return bdFalsa.stream().filter(t -> t.cedulaIdentidad().equals(ci)).findFirst();
            }

            @Override
            public List<Trabajador> listarTodos() {
                return new ArrayList<>(bdFalsa);
            }

            @Override
            public Trabajador guardar(Trabajador trabajador) {
                bdFalsa.removeIf(t -> t.id().equals(trabajador.id()));
                bdFalsa.add(trabajador);
                return trabajador;
            }

            @Override
            public Optional<Trabajador> buscarPorId(UUID id) {
                return bdFalsa.stream().filter(t -> t.id().equals(id)).findFirst();
            }
        };

        service = new GestionTrabajadorService(mockRepositorio, mockHasher);
    }

    @Test
    @DisplayName("registrarTrabajador: Detecta y rechaza un nombre de usuario duplicado")
    void registrarTrabajador_UsuarioDuplicado_RetornaError() {
        Trabajador existente = new Trabajador(
                UUID.randomUUID(), "Juan Perez", "1234567", "71234567",
                new Credencial("juanp", "hash123"), Rol.VENDEDOR, EstadoTrabajador.ACTIVO, Optional.empty()
        );
        bdFalsa.add(existente);

        Result<Trabajador, ErrorDominio> result = service.registrarTrabajador(
                "Juan Perez Segundo", "7654321", "77777777", "juanp", "Password123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(result.isError());
        assertInstanceOf(ErrorDominio.UsuarioYaExiste.class, result.getError());
        assertEquals("juanp", ((ErrorDominio.UsuarioYaExiste) result.getError()).usuario());
    }

    @Test
    @DisplayName("registrarTrabajador: Detecta y rechaza una Cédula de Identidad (CI) duplicada")
    void registrarTrabajador_CIDuplicada_RetornaError() {
        Trabajador existente = new Trabajador(
                UUID.randomUUID(), "Maria Lopez", "1234567 LP", "71234567",
                new Credencial("marial", "hash123"), Rol.VENDEDOR, EstadoTrabajador.ACTIVO, Optional.empty()
        );
        bdFalsa.add(existente);

        Result<Trabajador, ErrorDominio> result = service.registrarTrabajador(
                "Maria Gomez", "1234567 LP", "78888888", "mariag", "Password123", Rol.VENDEDOR, Optional.empty()
        );

        assertTrue(result.isError());
        assertInstanceOf(ErrorDominio.CedulaIdentidadYaExiste.class, result.getError());
        assertEquals("1234567 LP", ((ErrorDominio.CedulaIdentidadYaExiste) result.getError()).ci());
    }

    @Test
    @DisplayName("registrarTrabajador: Registra exitosamente al trabajador si usuario y CI son únicos")
    void registrarTrabajador_DatosValidosYUnicos_RetornaOk() {
        Result<Trabajador, ErrorDominio> result = service.registrarTrabajador(
                "Roberto Gomez", "8888888", "79999999", "robertog", "Password123", Rol.ADMINISTRADOR, Optional.empty()
        );

        assertTrue(result.isOk());
        Trabajador t = result.getValue();
        assertEquals("Roberto Gomez", t.nombreCompleto());
        assertEquals("8888888", t.cedulaIdentidad());
        assertEquals("79999999", t.telefono());
        assertEquals("robertog", t.credencial().usuario());
        assertEquals("hashed_Password123", t.credencial().passwordHash());
    }
}
