package com.psicodeli.core.dominio.validador;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.producto.CategoriaProducto;

import java.time.LocalDate;
import java.time.Period;

public class ValidadorVentaEdad {

    private ValidadorVentaEdad() {}

    public static boolean esProductoRestringidoParaMenores(CategoriaProducto categoria) {
        if (categoria == null) return false;
        return switch (categoria) {
            case CIGARRILLO, CERVEZA, TRAGO -> true;
            default -> false;
        };
    }

    public static Result<Integer, ErrorDominio> validarEdadVenta(
            LocalDate fechaNacimientoCliente, 
            CategoriaProducto categoriaProducto, 
            LocalDate fechaVenta) {
        
        if (fechaNacimientoCliente == null || fechaVenta == null) {
            return Result.error(new ErrorDominio.ValorInvalido("Las fechas de nacimiento y venta no pueden ser nulas"));
        }

        if (fechaNacimientoCliente.isAfter(fechaVenta)) {
            return Result.error(new ErrorDominio.ValorInvalido("La fecha de nacimiento no puede ser posterior a la fecha de venta"));
        }

        int edad = Period.between(fechaNacimientoCliente, fechaVenta).getYears();

        if (esProductoRestringidoParaMenores(categoriaProducto) && edad < 18) {
            return Result.error(new ErrorDominio.VentaNoPermitidaMenorDeEdad(edad, categoriaProducto.name()));
        }

        return Result.ok(edad);
    }
}
