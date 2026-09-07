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

    @Test
    void generarCodigo_conPrefijoYCorrelativoFormateado() {
        assertEquals("CER-0001", ProductoFunciones.generarCodigo(CategoriaProducto.CERVEZA, 1));
        assertEquals("GAS-0042", ProductoFunciones.generarCodigo(CategoriaProducto.GASEOSA, 42));
        assertEquals("CIG-0999", ProductoFunciones.generarCodigo(CategoriaProducto.CIGARRILLO, 999));
        assertEquals("REF-1234", ProductoFunciones.generarCodigo(CategoriaProducto.REFRESCO, 1234));
        assertEquals("TRA-0005", ProductoFunciones.generarCodigo(CategoriaProducto.TRAGO, 5));
        assertEquals("COM-0003", ProductoFunciones.generarCodigo(CategoriaProducto.COMBO, 3));
    }

    @Test
    void crearProducto_conCodigoNuloOVacio_autogeneraCodigoConPrefijo() {
        Presentacion pres = new Presentacion("Lata", new BigDecimal("350"), UnidadMedida.ML);

        var resNulo = ProductoFunciones.crearProducto(
                null,
                "Coca Cola",
                "Coca Cola Company",
                CategoriaProducto.GASEOSA,
                pres,
                new BigDecimal("7.00"),
                20,
                Optional.empty()
        );

        assertTrue(resNulo.isOk());
        Producto pNulo = resNulo.getValue();
        assertNotNull(pNulo.id());
        assertEquals("GAS-0001", pNulo.codigo());

        var resVacio = ProductoFunciones.crearProducto(
                "   ",
                "Ron Abuelo",
                "Varela Hermanos",
                CategoriaProducto.TRAGO,
                pres,
                new BigDecimal("80.00"),
                5,
                Optional.empty()
        );

        assertTrue(resVacio.isOk());
        assertEquals("TRA-0001", resVacio.getValue().codigo());
    }
}
