package com.psicodeli.core.dominio.producto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Agregado/Entidad inmutable de Dominio que representa un Producto (Documento F).
 */
public record Producto(
    UUID id,
    String codigo,
    String nombre,
    String marca,
    CategoriaProducto categoria,
    Presentacion presentacion,
    BigDecimal precio,
    EstadoProducto estado,
    Integer stock,
    Optional<BigDecimal> descuento,
    Optional<DetalleCigarrillo> detalleCigarrillo
) {
    public boolean esCigarrillo() {
        return categoria == CategoriaProducto.CIGARRILLO || detalleCigarrillo.isPresent();
    }
}
