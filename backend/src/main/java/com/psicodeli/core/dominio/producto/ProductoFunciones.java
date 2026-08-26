package com.psicodeli.core.dominio.producto;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.compartido.UuidV7;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Contenedor de Funciones Puras para el Dominio de Productos (Documento F - Sección 5 y 6).
 */
public final class ProductoFunciones {

    private ProductoFunciones() {}

    public static Result<Producto, ErrorDominio> crearProducto(
            String codigo,
            String nombre,
            String marca,
            CategoriaProducto categoria,
            Presentacion presentacion,
            BigDecimal precio,
            Integer stockInicial,
            Optional<DetalleCigarrillo> detalleCigarrillo) {

        if (nombre == null || nombre.isBlank()) {
            return Result.error(new ErrorDominio.ValorInvalido("El nombre del producto no puede estar vacío"));
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            return Result.error(new ErrorDominio.PrecioInvalido());
        }
        if (stockInicial == null || stockInicial < 0) {
            stockInicial = 0;
        }

        String codigoFinal = (codigo != null && !codigo.isBlank()) ? codigo.trim().toUpperCase() : "PROD-" + System.currentTimeMillis();
        String marcaFinal = (marca != null) ? marca.trim() : "";
        Presentacion presentacionFinal = (presentacion != null) ? presentacion : new Presentacion("Unidad", BigDecimal.ONE, UnidadMedida.UNIDAD);
        CategoriaProducto catFinal = (categoria != null) ? categoria : CategoriaProducto.CERVEZA;

        Producto producto = new Producto(
                UuidV7.generar(),
                codigoFinal,
                nombre.trim(),
                marcaFinal,
                catFinal,
                presentacionFinal,
                precio,
                EstadoProducto.ACTIVO,
                stockInicial,
                Optional.empty(),
                detalleCigarrillo
        );

        return Result.ok(producto);
    }

    public static Producto cambiarEstado(Producto producto, EstadoProducto nuevoEstado) {
        return new Producto(
                producto.id(),
                producto.codigo(),
                producto.nombre(),
                producto.marca(),
                producto.categoria(),
                producto.presentacion(),
                producto.precio(),
                nuevoEstado,
                producto.stock(),
                producto.descuento(),
                producto.detalleCigarrillo()
        );
    }

    /**
     * Regla RN17 y Sección 6.3 del Documento F:
     * Transforma N cajetillas cerradas en unidades sueltas:
     * stockCajetillas -= cantidadCajetillas
     * stockUnidadesSueltas += cantidadCajetillas * unidadesPorCajetilla
     */
    public static Result<Producto, ErrorDominio> abrirCajetilla(Producto producto, int cantidadCajetillas) {
        if (!producto.esCigarrillo() || producto.detalleCigarrillo().isEmpty()) {
            return Result.error(new ErrorDominio.CigarrilloInvalido());
        }
        if (cantidadCajetillas <= 0) {
            return Result.error(new ErrorDominio.ValorInvalido("La cantidad de cajetillas a abrir debe ser mayor a 0"));
        }
        if (producto.stock() < cantidadCajetillas) {
            return Result.error(new ErrorDominio.CajetillaInsuficiente());
        }

        DetalleCigarrillo actual = producto.detalleCigarrillo().get();
        int nuevoStockCajetillas = producto.stock() - cantidadCajetillas;
        int nuevasSueltas = actual.stockUnidadesSueltas() + (cantidadCajetillas * actual.unidadesPorCajetilla());

        DetalleCigarrillo nuevoDetalle = new DetalleCigarrillo(
                actual.unidadesPorCajetilla(),
                nuevasSueltas,
                actual.precioUnidadSuelta()
        );

        Producto actualizado = new Producto(
                producto.id(),
                producto.codigo(),
                producto.nombre(),
                producto.marca(),
                producto.categoria(),
                producto.presentacion(),
                producto.precio(),
                producto.estado(),
                nuevoStockCajetillas,
                producto.descuento(),
                Optional.of(nuevoDetalle)
        );

        return Result.ok(actualizado);
    }
}
