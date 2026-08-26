package com.psicodeli.core.dominio.producto;

import com.psicodeli.core.dominio.compartido.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductoFuncionesTest {

    @Test
    void crearProducto_exitoso() {
        Presentacion pres = new Presentacion("Botella", new BigDecimal("620"), UnidadMedida.ML);
        var res = ProductoFunciones.crearProducto(
                "CER-001",
                "Paceña",
                "CBN",
                CategoriaProducto.CERVEZA,
                pres,
                new BigDecimal("15.00"),
                50,
                Optional.empty()
        );

        assertTrue(res.isOk());
        Producto p = res.getValue();
        assertEquals("CER-001", p.codigo());
        assertEquals("Paceña", p.nombre());
        assertEquals("CBN", p.marca());
        assertEquals(50, p.stock());
        assertEquals("Botella 620 ML", p.presentacion().toTextoFormateado());
    }

    @Test
    void abrirCajetilla_transformaCajetillasEnUnidadesSueltas() {
        Presentacion pres = new Presentacion("Cajetilla", new BigDecimal("20"), UnidadMedida.UNIDAD);
        DetalleCigarrillo cigDetalle = new DetalleCigarrillo(20, 0, new BigDecimal("1.50"));

        var res = ProductoFunciones.crearProducto(
                "CIG-001",
                "Camel Blue",
                "Camel",
                CategoriaProducto.CIGARRILLO,
                pres,
                new BigDecimal("25.00"),
                10, // 10 cajetillas en stock
                Optional.of(cigDetalle)
        );

        assertTrue(res.isOk());
        Producto cig = res.getValue();

        // Abrir 1 cajetilla
        var resAbrir = ProductoFunciones.abrirCajetilla(cig, 1);
        assertTrue(resAbrir.isOk());

        Producto cigModificado = resAbrir.getValue();
        assertEquals(9, cigModificado.stock()); // 9 cajetillas restantes
        assertEquals(20, cigModificado.detalleCigarrillo().get().stockUnidadesSueltas()); // 20 sueltos disponibles
    }
}
