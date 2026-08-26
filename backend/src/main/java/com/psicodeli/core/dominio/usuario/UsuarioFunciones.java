package com.psicodeli.core.dominio.usuario;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;

import com.psicodeli.core.dominio.compartido.UuidV7;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public class UsuarioFunciones {

    public static Result<Trabajador, ErrorDominio> autenticar(
            String usuario, 
            String intentoPlano,
            List<Trabajador> trabajadores,
            BiFunction<String, String, Boolean> verificarHash) {
        
        Optional<Trabajador> trabajadorOpt = trabajadores.stream()
                .filter(t -> t.credencial().usuario().equals(usuario))
                .findFirst();

        if (trabajadorOpt.isEmpty()) {
            return Result.error(new ErrorDominio.TrabajadorNoEncontrado());
        }

        Trabajador trabajador = trabajadorOpt.get();

        if (trabajador.estado() != EstadoTrabajador.ACTIVO) {
            return Result.error(new ErrorDominio.TrabajadorInactivo());
        }

        if (!verificarHash.apply(intentoPlano, trabajador.credencial().passwordHash())) {
            return Result.error(new ErrorDominio.CredencialesInvalidas());
        }

        return Result.ok(trabajador);
    }

    public static boolean puedeEjecutar(Rol rol, String accion) {
        if (rol == Rol.ADMINISTRADOR) {
            return true;
        }
        
        // VENDEDOR permissions
        return switch (accion) {
            case "REGISTRAR_VENTA", "CONSULTAR_INVENTARIO", "ABRIR_CAJETILLA" -> true;
            default -> false;
        };
    }

    public static Result<Trabajador, ErrorDominio> crearTrabajador(
            String nombreCompleto,
            String usuario,
            String passwordHash,
            Rol rol,
            Optional<HorarioAsignado> horarioAsignado) {
        
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("Nombre no puede estar vacío"));
        }
        if (usuario == null || usuario.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("Usuario no puede estar vacío"));
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("Password hash no puede estar vacío"));
        }

        try {
            Credencial credencial = new Credencial(usuario, passwordHash);
            Trabajador trabajador = new Trabajador(
                UuidV7.generar(), 
                nombreCompleto, 
                credencial, 
                rol != null ? rol : Rol.VENDEDOR, 
                EstadoTrabajador.ACTIVO, 
                horarioAsignado
            );
            return Result.ok(trabajador);
        } catch (IllegalArgumentException e) {
            return Result.error(new ErrorDominio.ValorInvalido(e.getMessage()));
        }
    }

    public static Trabajador cambiarEstado(Trabajador trabajador, EstadoTrabajador nuevoEstado) {
        return new Trabajador(
            trabajador.id(),
            trabajador.nombreCompleto(),
            trabajador.credencial(),
            trabajador.rol(),
            nuevoEstado,
            trabajador.horarioAsignado()
        );
    }

    public static Trabajador cambiarPassword(Trabajador trabajador, String nuevoPasswordHash) {
        Credencial nuevaCredencial = new Credencial(trabajador.credencial().usuario(), nuevoPasswordHash);
        return new Trabajador(
            trabajador.id(),
            trabajador.nombreCompleto(),
            nuevaCredencial,
            trabajador.rol(),
            trabajador.estado(),
            trabajador.horarioAsignado()
        );
    }

    public static Result<RegistroSesion, ErrorDominio> iniciarSesion(
            Trabajador trabajador, 
            LocalDateTime fechaHora) {
        if (trabajador.estado() != EstadoTrabajador.ACTIVO) {
            return Result.error(new ErrorDominio.TrabajadorInactivo());
        }
        
        RegistroSesion sesion = new RegistroSesion(
            UuidV7.generar(), 
            trabajador.id(), 
            fechaHora, 
            Optional.empty()
        );
        return Result.ok(sesion);
    }
}
