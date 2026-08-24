package com.psicodeli.core.infraestructura.persistencia.repositorios;

import com.psicodeli.core.infraestructura.persistencia.entidades.TrabajadorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrabajadorJpaRepository extends JpaRepository<TrabajadorJpaEntity, UUID> {
    Optional<TrabajadorJpaEntity> findByUsuario(String usuario);
}
