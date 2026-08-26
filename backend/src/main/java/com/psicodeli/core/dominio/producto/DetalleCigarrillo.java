package com.psicodeli.core.dominio.producto;

import java.math.BigDecimal;

/**
 * Value Object inmutable específico para productos de tipo Cigarrillo (Sección 6 del Documento F).
 * Encapsula la gestión de cajetillas cerradas y unidades sueltas.
 */
public record DetalleCigarrillo(
    int unidadesPorCajetilla,
    int stockUnidadesSueltas,
    BigDecimal precioUnidadSuelta
) {
    public DetalleCigarrillo {
        if (unidadesPorCajetilla <= 0) {
            unidadesPorCajetilla = 20; // Valor predeterminado estándar
        }
        if (stockUnidadesSueltas < 0) {
            stockUnidadesSueltas = 0;
        }
        if (precioUnidadSuelta == null || precioUnidadSuelta.compareTo(BigDecimal.ZERO) < 0) {
            precioUnidadSuelta = BigDecimal.ZERO;
        }
    }
}
