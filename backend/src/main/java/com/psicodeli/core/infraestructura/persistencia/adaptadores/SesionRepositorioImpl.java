package com.psicodeli.core.infraestructura.persistencia.adaptadores;

import com.psicodeli.core.aplicacion.usuario.puertos.SesionRepositorio;
import com.psicodeli.core.dominio.usuario.RegistroSesion;
import com.psicodeli.core.infraestructura.persistencia.entidades.SesionJpaEntity;
import com.psicodeli.core.infraestructura.persistencia.repositorios.SesionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SesionRepositorioImpl implements SesionRepositorio {

    private final SesionJpaRepository jpaRepository;

    public SesionRepositorioImpl(SesionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RegistroSesion guardar(RegistroSesion sesion) {
        SesionJpaEntity entity = toEntity(sesion);
        return toDominio(jpaRepository.save(entity));
    }

    @Override
    public List<RegistroSesion> listarPorTrabajador(UUID trabajadorId) {
        return jpaRepository.findByTrabajadorId(trabajadorId).stream()
                .map(this::toDominio)
                .collect(Collectors.toList());
    }

    private RegistroSesion toDominio(SesionJpaEntity entity) {
        return new RegistroSesion(
                entity.getId(),
                entity.getTrabajadorId(),
                entity.getInicio(),
                Optional.ofNullable(entity.getFin())
        );
    }

    private SesionJpaEntity toEntity(RegistroSesion dominio) {
        SesionJpaEntity entity = new SesionJpaEntity();
        entity.setId(dominio.id());
        entity.setTrabajadorId(dominio.trabajadorId());
        entity.setInicio(dominio.inicio());
        dominio.fin().ifPresent(entity::setFin);
        return entity;
    }
}
