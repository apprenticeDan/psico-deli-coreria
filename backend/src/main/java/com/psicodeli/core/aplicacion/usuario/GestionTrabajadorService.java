package com.psicodeli.core.aplicacion.usuario;

import com.psicodeli.core.aplicacion.usuario.puertos.PasswordHasher;
import com.psicodeli.core.aplicacion.usuario.puertos.TrabajadorRepositorio;
import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.usuario.HorarioAsignado;
import com.psicodeli.core.dominio.usuario.Rol;
import com.psicodeli.core.dominio.usuario.Trabajador;
import com.psicodeli.core.dominio.usuario.UsuarioFunciones;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        
        // Verifica que no exista
        if (trabajadorRepositorio.buscarPorUsuario(usuario).isPresent()) {
            return Result.error(new ErrorDominio.ValorInvalido("El usuario ya existe"));
        }

        String hash = passwordHasher.hash(passwordPlano);

        return UsuarioFunciones.crearTrabajador(
                nombreCompleto, usuario, hash, rol, horarioAsignado
        ).map(trabajadorRepositorio::guardar);
    }

    public List<Trabajador> listarTrabajadores() {
        return trabajadorRepositorio.listarTodos();
    }
}
