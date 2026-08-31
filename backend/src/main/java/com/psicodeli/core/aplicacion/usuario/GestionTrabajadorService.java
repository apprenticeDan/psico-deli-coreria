package com.psicodeli.core.aplicacion.usuario;

import com.psicodeli.core.aplicacion.usuario.puertos.PasswordHasher;
import com.psicodeli.core.aplicacion.usuario.puertos.TrabajadorRepositorio;
import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.usuario.EstadoTrabajador;
import com.psicodeli.core.dominio.usuario.HorarioAsignado;
import com.psicodeli.core.dominio.usuario.Rol;
import com.psicodeli.core.dominio.usuario.Trabajador;
import com.psicodeli.core.dominio.usuario.UsuarioFunciones;
import com.psicodeli.core.dominio.validador.ValidadorInput;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GestionTrabajadorService {

    private final TrabajadorRepositorio trabajadorRepositorio;
    private final PasswordHasher passwordHasher;

    public GestionTrabajadorService(
            TrabajadorRepositorio trabajadorRepositorio,
            PasswordHasher passwordHasher) {
        this.trabajadorRepositorio = trabajadorRepositorio;
        this.passwordHasher = passwordHasher;
    }

    public Result<Trabajador, ErrorDominio> registrarTrabajador(
            String nombreCompleto, String usuario, String passwordPlano, 
            Rol rol, Optional<HorarioAsignado> horarioAsignado) {
        return registrarTrabajador(nombreCompleto, String.valueOf((int) (Math.random() * 90000000) + 1000000), "70000000", usuario, passwordPlano, rol, horarioAsignado);
    }

    public Result<Trabajador, ErrorDominio> registrarTrabajador(
            String nombreCompleto, String cedulaIdentidad, String telefono,
            String usuario, String passwordPlano, 
            Rol rol, Optional<HorarioAsignado> horarioAsignado) {
        
        // Unicidad de usuario
        if (usuario != null && trabajadorRepositorio.buscarPorUsuario(usuario.trim()).isPresent()) {
            return Result.error(new ErrorDominio.UsuarioYaExiste(usuario.trim()));
        }

        // Unicidad de CI
        if (cedulaIdentidad != null && trabajadorRepositorio.buscarPorCedulaIdentidad(cedulaIdentidad.trim()).isPresent()) {
            return Result.error(new ErrorDominio.CedulaIdentidadYaExiste(cedulaIdentidad.trim()));
        }
        // 3. Validar password PLANO
            Result<String, ErrorDominio> valPass =
                    ValidadorInput.validarPassword(passwordPlano);

            if (valPass.isError()) {
                return Result.error(valPass.getError());
            }

        String hash = passwordHasher.hash(passwordPlano != null ? passwordPlano : "");

        return UsuarioFunciones.crearTrabajador(
                nombreCompleto, cedulaIdentidad, telefono, usuario, hash, rol, horarioAsignado
        ).map(trabajadorRepositorio::guardar);
    }

    public Result<Trabajador, ErrorDominio> cambiarEstado(UUID id, EstadoTrabajador nuevoEstado) {
        Optional<Trabajador> opt = trabajadorRepositorio.buscarPorId(id);
        if (opt.isEmpty()) {
            return Result.error(new ErrorDominio.TrabajadorNoEncontrado());
        }

        Trabajador modificado = UsuarioFunciones.cambiarEstado(opt.get(), nuevoEstado);
        Trabajador guardado = trabajadorRepositorio.guardar(modificado);
        return Result.ok(guardado);
    }

    public Result<Trabajador, ErrorDominio> cambiarPassword(UUID id, String nuevaPasswordPlana) {
        if (nuevaPasswordPlana == null || nuevaPasswordPlana.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("La contraseña no puede estar vacía"));
        }

        Optional<Trabajador> opt = trabajadorRepositorio.buscarPorId(id);
        if (opt.isEmpty()) {
            return Result.error(new ErrorDominio.TrabajadorNoEncontrado());
        }

        String nuevoHash = passwordHasher.hash(nuevaPasswordPlana);
        Trabajador modificado = UsuarioFunciones.cambiarPassword(opt.get(), nuevoHash);
        Trabajador guardado = trabajadorRepositorio.guardar(modificado);
        return Result.ok(guardado);
    }

    public List<Trabajador> listarTrabajadores() {
        return trabajadorRepositorio.listarTodos();
    }
}
