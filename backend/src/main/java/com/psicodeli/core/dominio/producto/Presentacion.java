package com.psicodeli.core.dominio.producto;

import java.math.BigDecimal;

/**
 * Value Object inmutable que representa la Presentación de un producto
 * (ej. Empaque: "Botella", Contenido: 620, Unidad: ML -> "Botella 620 ML").
 */
public record Presentacion(
    String empaque,
    BigDecimal contenido,
    UnidadMedida unidad
) {
    public Presentacion {
        if (empaque == null || empaque.isBlank()) {
            empaque = "Unidad";
        }
        if (contenido == null || contenido.compareTo(BigDecimal.ZERO) <= 0) {
            contenido = BigDecimal.ONE;
        }
        if (unidad == null) {
            unidad = UnidadMedida.UNIDAD;
        }
    }

    public String toTextoFormateado() {
        String num = contenido.stripTrailingZeros().toPlainString();
        return String.format("%s %s %s", empaque, num, unidad);
    }
}
