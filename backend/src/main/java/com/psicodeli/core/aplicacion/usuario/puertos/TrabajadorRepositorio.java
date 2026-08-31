package com.psicodeli.core.aplicacion.usuario.puertos;

import com.psicodeli.core.dominio.usuario.Trabajador;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrabajadorRepositorio {
    Optional<Trabajador> buscarPorUsuario(String usuario);
    Optional<Trabajador> buscarPorCedulaIdentidad(String ci);
    List<Trabajador> listarTodos();
    Trabajador guardar(Trabajador trabajador);
    Optional<Trabajador> buscarPorId(UUID id);
}
