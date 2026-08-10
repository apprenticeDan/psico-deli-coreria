package com.psicodeli.core.dominio.producto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Value Object inmutable que representa el concepto de Producto
 * en nuestro Dominio Puro (Documento F).
 */
public record Producto(
    UUID id,
    String nombre,
    CategoriaProducto categoria,
    BigDecimal precio,
    EstadoProducto estado,
    Integer stock,
    Optional<BigDecimal> descuento
) {
    // Ejemplo de factory method funcional (pura) para creación inicial
    public static Producto crear(String nombre, CategoriaProducto categoria, BigDecimal precio) {
        return new Producto(
            UUID.randomUUID(), 
            nombre, 
            categoria, 
            precio, 
            EstadoProducto.ACTIVO, 
            0, 
            Optional.empty()
        );
    }
}
