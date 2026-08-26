package com.psicodeli.core.infraestructura.web.dto;

import com.psicodeli.core.dominio.producto.CategoriaProducto;
import com.psicodeli.core.dominio.producto.UnidadMedida;

import java.math.BigDecimal;

public record ProductoRequest(
    String codigo,
    String nombre,
    String marca,
    CategoriaProducto categoria,
    String empaque,
    BigDecimal contenido,
    UnidadMedida unidad,
    BigDecimal precio,
    Integer stock,
    Boolean esCigarrillo,
    Integer unidadesPorCajetilla,
    BigDecimal precioUnidadSuelta
) {}
