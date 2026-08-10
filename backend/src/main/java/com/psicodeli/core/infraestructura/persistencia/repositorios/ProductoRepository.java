package com.psicodeli.core.infraestructura.persistencia.repositorios;

import com.psicodeli.core.infraestructura.persistencia.entidades.ProductoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoJpaEntity, UUID> {
}
