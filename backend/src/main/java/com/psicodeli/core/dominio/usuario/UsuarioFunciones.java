package com.psicodeli.core.dominio.usuario;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;

import com.psicodeli.core.dominio.compartido.UuidV7;
import com.psicodeli.core.dominio.validador.ValidadorInput;

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
        
        Result<String, ErrorDominio> valUsr = ValidadorInput.validarNombreUsuario(usuario);
        if (valUsr.isError()) {
            return Result.error(valUsr.getError());
        }

        Result<String, ErrorDominio> valPass = ValidadorInput.validarPassword(intentoPlano);
        if (valPass.isError()) {
            return Result.error(valPass.getError());
        }

        Optional<Trabajador> trabajadorOpt = trabajadores.stream()
                .filter(t -> t.credencial().usuario().equals(valUsr.getValue()))
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
        return crearTrabajador(nombreCompleto, "1234567", "70000000", usuario, passwordHash, rol, horarioAsignado);
    }

    public static Result<Trabajador, ErrorDominio> crearTrabajador(
            String nombreCompleto,
            String cedulaIdentidad,
            String telefono,
            String usuario,
            String passwordHash,
            Rol rol,
            Optional<HorarioAsignado> horarioAsignado) {
        
        Result<String, ErrorDominio> valNombre = ValidadorInput.validarNombrePersona(nombreCompleto);
        if (valNombre.isError()) return Result.error(valNombre.getError());

        Result<String, ErrorDominio> valCi = ValidadorInput.validarCedulaIdentidad(cedulaIdentidad);
        if (valCi.isError()) return Result.error(valCi.getError());

        Result<String, ErrorDominio> valTel = ValidadorInput.validarTelefono(telefono);
        if (valTel.isError()) return Result.error(valTel.getError());

        Result<String, ErrorDominio> valUsr = ValidadorInput.validarNombreUsuario(usuario);
        if (valUsr.isError()) return Result.error(valUsr.getError());

        if (passwordHash == null || passwordHash.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("Password hash no puede estar vacío"));
        }

        try {
            Credencial credencial = new Credencial(valUsr.getValue(), passwordHash);
            Trabajador trabajador = new Trabajador(
                UuidV7.generar(), 
                valNombre.getValue(), 
                valCi.getValue(),
                valTel.getValue(),
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
            trabajador.cedulaIdentidad(),
            trabajador.telefono(),
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
            trabajador.cedulaIdentidad(),
            trabajador.telefono(),
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
