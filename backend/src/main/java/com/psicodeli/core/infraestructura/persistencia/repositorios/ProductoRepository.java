package com.psicodeli.core.infraestructura.persistencia.repositorios;

import com.psicodeli.core.infraestructura.persistencia.entidades.ProductoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoJpaEntity, UUID> {

    @Query("SELECT p.codigo FROM ProductoJpaEntity p WHERE p.codigo LIKE CONCAT(:prefijo, '%')")
    List<String> findCodigosByPrefijo(@Param("prefijo") String prefijo);
}
