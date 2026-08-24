package com.psicodeli.core.infraestructura.persistencia.repositorios;

import com.psicodeli.core.infraestructura.persistencia.entidades.SesionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SesionJpaRepository extends JpaRepository<SesionJpaEntity, UUID> {
    List<SesionJpaEntity> findByTrabajadorId(UUID trabajadorId);
}
