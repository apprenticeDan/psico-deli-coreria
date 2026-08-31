package com.psicodeli.core.dominio.validador;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.producto.CategoriaProducto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorVentaEdadTest {

    private final LocalDate hoy = LocalDate.of(2026, 8, 31);

    @Test
    @DisplayName("validarEdadVenta: Rechaza la venta de cigarrillos a menores de 18 años")
    void validarEdadVenta_MenorDe18AnosComprandoCigarrillos_RetornaError() {
        LocalDate fechaNacimiento17 = hoy.minusYears(17);
        
        Result<Integer, ErrorDominio> result = ValidadorVentaEdad.validarEdadVenta(
                fechaNacimiento17, 
                CategoriaProducto.CIGARRILLO, 
                hoy
        );

        assertTrue(result.isError());
        assertInstanceOf(ErrorDominio.VentaNoPermitidaMenorDeEdad.class, result.getError());
        ErrorDominio.VentaNoPermitidaMenorDeEdad err = (ErrorDominio.VentaNoPermitidaMenorDeEdad) result.getError();
        assertEquals(17, err.edadCalculada());
        assertEquals("CIGARRILLO", err.categoria());
    }

    @Test
    @DisplayName("validarEdadVenta: Rechaza la venta de cerveza y tragos a menores de 18 años")
    void validarEdadVenta_MenorDe18AnosComprandoAlcohol_RetornaError() {
        LocalDate fechaNacimiento17 = hoy.minusYears(17);

        Result<Integer, ErrorDominio> rCerveza = ValidadorVentaEdad.validarEdadVenta(
                fechaNacimiento17, CategoriaProducto.CERVEZA, hoy
        );
        Result<Integer, ErrorDominio> rTrago = ValidadorVentaEdad.validarEdadVenta(
                fechaNacimiento17, CategoriaProducto.TRAGO, hoy
        );

        assertTrue(rCerveza.isError());
        assertTrue(rTrago.isError());
    }

    @Test
    @DisplayName("validarEdadVenta: Permite la venta de productos no restringidos (gaseosa/refresco) a menores de 18 años")
    void validarEdadVenta_MenorDe18AnosComprandoGaseosa_RetornaOk() {
        LocalDate fechaNacimiento15 = hoy.minusYears(15);

        Result<Integer, ErrorDominio> result = ValidadorVentaEdad.validarEdadVenta(
                fechaNacimiento15, CategoriaProducto.GASEOSA, hoy
        );

        assertTrue(result.isOk());
        assertEquals(15, result.getValue());
    }

    @Test
    @DisplayName("validarEdadVenta: Permite la venta de cerveza/cigarrillos a cliente con 18 años cumplidos exactamente hoy")
    void validarEdadVenta_Cumple18Hoy_RetornaOk() {
        LocalDate cumple18Hoy = hoy.minusYears(18);

        Result<Integer, ErrorDominio> result = ValidadorVentaEdad.validarEdadVenta(
                cumple18Hoy, CategoriaProducto.CERVEZA, hoy
        );

        assertTrue(result.isOk());
        assertEquals(18, result.getValue());
    }

    @Test
    @DisplayName("validarEdadVenta: Rechaza si el cliente cumple 18 años mañana (aún tiene 17 años hoy)")
    void validarEdadVenta_Cumple18Manana_RetornaError() {
        LocalDate cumple18Manana = hoy.minusYears(18).plusDays(1);

        Result<Integer, ErrorDominio> result = ValidadorVentaEdad.validarEdadVenta(
                cumple18Manana, CategoriaProducto.CIGARRILLO, hoy
        );

        assertTrue(result.isError());
        assertInstanceOf(ErrorDominio.VentaNoPermitidaMenorDeEdad.class, result.getError());
        assertEquals(17, ((ErrorDominio.VentaNoPermitidaMenorDeEdad) result.getError()).edadCalculada());
    }
}
