package com.psicodeli.core;

import com.psicodeli.core.aplicacion.usuario.GestionTrabajadorService;
import com.psicodeli.core.aplicacion.usuario.puertos.TrabajadorRepositorio;
import com.psicodeli.core.dominio.usuario.Rol;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GestionTrabajadorService gestionTrabajadorService;
    private final TrabajadorRepositorio trabajadorRepositorio;

    public DataInitializer(GestionTrabajadorService gestionTrabajadorService, TrabajadorRepositorio trabajadorRepositorio) {
        this.gestionTrabajadorService = gestionTrabajadorService;
        this.trabajadorRepositorio = trabajadorRepositorio;
    }

    @Override
    public void run(String... args) throws Exception {
        if (trabajadorRepositorio.buscarPorUsuario("admin").isEmpty()) {
            gestionTrabajadorService.registrarTrabajador(
                    "Administrador Sistema",
                    "admin",
                    "admin123",
                    Rol.ADMINISTRADOR,
                    Optional.empty()
            );
            System.out.println("Usuario admin creado con éxito (admin/admin123)");
        }
    }
}
