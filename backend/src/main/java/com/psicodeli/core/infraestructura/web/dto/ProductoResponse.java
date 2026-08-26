package com.psicodeli.core.infraestructura.web.dto;

import com.psicodeli.core.dominio.producto.CategoriaProducto;
import com.psicodeli.core.dominio.producto.EstadoProducto;
import com.psicodeli.core.dominio.producto.Producto;
import com.psicodeli.core.dominio.producto.UnidadMedida;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoResponse(
    UUID id,
    String codigo,
    String nombre,
    String marca,
    CategoriaProducto categoria,
    String empaque,
    BigDecimal contenido,
    UnidadMedida unidad,
    String presentacionTexto,
    BigDecimal precio,
    EstadoProducto estado,
    Integer stock,
    Boolean esCigarrillo,
    Integer unidadesPorCajetilla,
    Integer stockUnidadesSueltas,
    BigDecimal precioUnidadSuelta
) {
    public static ProductoResponse fromDomain(Producto p) {
        String empaque = p.presentacion() != null ? p.presentacion().empaque() : "Unidad";
        BigDecimal contenido = p.presentacion() != null ? p.presentacion().contenido() : BigDecimal.ONE;
        UnidadMedida unidad = p.presentacion() != null ? p.presentacion().unidad() : UnidadMedida.UNIDAD;
        String presentacionTexto = p.presentacion() != null ? p.presentacion().toTextoFormateado() : "Unidad 1 UNIDAD";

        boolean esCigarrillo = p.esCigarrillo();
        Integer unidadesPorCajetilla = null;
        Integer stockUnidadesSueltas = null;
        BigDecimal precioUnidadSuelta = null;

        if (p.detalleCigarrillo().isPresent()) {
            var det = p.detalleCigarrillo().get();
            unidadesPorCajetilla = det.unidadesPorCajetilla();
            stockUnidadesSueltas = det.stockUnidadesSueltas();
            precioUnidadSuelta = det.precioUnidadSuelta();
        }

        return new ProductoResponse(
                p.id(),
                p.codigo(),
                p.nombre(),
                p.marca(),
                p.categoria(),
                empaque,
                contenido,
                unidad,
                presentacionTexto,
                p.precio(),
                p.estado(),
                p.stock(),
                esCigarrillo,
                unidadesPorCajetilla,
                stockUnidadesSueltas,
                precioUnidadSuelta
        );
    }
}
