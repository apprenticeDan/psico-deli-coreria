package com.psicodeli.core.infraestructura.persistencia.adaptadores;

import com.psicodeli.core.aplicacion.usuario.puertos.TrabajadorRepositorio;
import com.psicodeli.core.dominio.usuario.Credencial;
import com.psicodeli.core.dominio.usuario.HorarioAsignado;
import com.psicodeli.core.dominio.usuario.Trabajador;
import com.psicodeli.core.infraestructura.persistencia.entidades.TrabajadorJpaEntity;
import com.psicodeli.core.infraestructura.persistencia.repositorios.TrabajadorJpaRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TrabajadorRepositorioImpl implements TrabajadorRepositorio {

    private final TrabajadorJpaRepository jpaRepository;

    public TrabajadorRepositorioImpl(TrabajadorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Trabajador> buscarPorUsuario(String usuario) {
        return jpaRepository.findByUsuario(usuario).map(this::toDominio);
    }

    @Override
    public Optional<Trabajador> buscarPorCedulaIdentidad(String ci) {
        return jpaRepository.findByCedulaIdentidad(ci).map(this::toDominio);
    }

    @Override
    public List<Trabajador> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDominio)
                .collect(Collectors.toList());
    }

    @Override
    public Trabajador guardar(Trabajador trabajador) {
        TrabajadorJpaEntity entity = toEntity(trabajador);
        return toDominio(jpaRepository.save(entity));
    }

    @Override
    public Optional<Trabajador> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDominio);
    }

    private Trabajador toDominio(TrabajadorJpaEntity entity) {
        Optional<HorarioAsignado> horario = Optional.empty();
        if (entity.getHorarioDias() != null && !entity.getHorarioDias().isBlank() && 
            entity.getHorarioHoraInicio() != null && entity.getHorarioHoraFin() != null) {
            
            Set<DayOfWeek> dias = Arrays.stream(entity.getHorarioDias().split(","))
                    .map(DayOfWeek::valueOf)
                    .collect(Collectors.toSet());
            
            horario = Optional.of(new HorarioAsignado(dias, entity.getHorarioHoraInicio(), entity.getHorarioHoraFin()));
        }

        String ci = entity.getCedulaIdentidad() != null ? entity.getCedulaIdentidad() : "1234567";
        String tel = entity.getTelefono() != null ? entity.getTelefono() : "70000000";

        return new Trabajador(
                entity.getId(),
                entity.getNombreCompleto(),
                ci,
                tel,
                new Credencial(entity.getUsuario(), entity.getPasswordHash()),
                entity.getRol(),
                entity.getEstado(),
                horario
        );
    }

    private TrabajadorJpaEntity toEntity(Trabajador dominio) {
        TrabajadorJpaEntity entity = new TrabajadorJpaEntity();
        entity.setId(dominio.id());
        entity.setNombreCompleto(dominio.nombreCompleto());
        entity.setCedulaIdentidad(dominio.cedulaIdentidad());
        entity.setTelefono(dominio.telefono());
        entity.setUsuario(dominio.credencial().usuario());
        entity.setPasswordHash(dominio.credencial().passwordHash());
        entity.setRol(dominio.rol());
        entity.setEstado(dominio.estado());

        dominio.horarioAsignado().ifPresent(h -> {
            entity.setHorarioDias(h.dias().stream().map(Enum::name).collect(Collectors.joining(",")));
            entity.setHorarioHoraInicio(h.horaInicio());
            entity.setHorarioHoraFin(h.horaFin());
        });

        return entity;
    }
}
