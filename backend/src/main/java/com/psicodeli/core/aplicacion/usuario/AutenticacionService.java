package com.psicodeli.core.aplicacion.usuario;

import com.psicodeli.core.aplicacion.usuario.puertos.JwtService;
import com.psicodeli.core.aplicacion.usuario.puertos.PasswordHasher;
import com.psicodeli.core.aplicacion.usuario.puertos.SesionRepositorio;
import com.psicodeli.core.aplicacion.usuario.puertos.TrabajadorRepositorio;
import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.usuario.RegistroSesion;
import com.psicodeli.core.dominio.usuario.Trabajador;
import com.psicodeli.core.dominio.usuario.UsuarioFunciones;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutenticacionService {

    private final TrabajadorRepositorio trabajadorRepositorio;
    private final SesionRepositorio sesionRepositorio;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;

    public AutenticacionService(
            TrabajadorRepositorio trabajadorRepositorio,
            SesionRepositorio sesionRepositorio,
            PasswordHasher passwordHasher,
            JwtService jwtService) {
        this.trabajadorRepositorio = trabajadorRepositorio;
        this.sesionRepositorio = sesionRepositorio;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
    }

    public Result<String, ErrorDominio> login(String usuario, String passwordPlano) {
        List<Trabajador> trabajadores = trabajadorRepositorio.listarTodos();

        Result<Trabajador, ErrorDominio> authResult = UsuarioFunciones.autenticar(
                usuario, passwordPlano, trabajadores, passwordHasher::verify
        );

        return authResult.flatMap(trabajador -> {
            Result<RegistroSesion, ErrorDominio> sesionResult = UsuarioFunciones.iniciarSesion(
                    trabajador, LocalDateTime.now()
            );

            return sesionResult.map(sesion -> {
                sesionRepositorio.guardar(sesion);
                return jwtService.generateToken(trabajador);
            });
        });
    }
}
