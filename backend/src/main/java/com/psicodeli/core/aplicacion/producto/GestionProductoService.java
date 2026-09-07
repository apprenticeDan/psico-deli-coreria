package com.psicodeli.core.aplicacion.producto;

import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.producto.*;
import com.psicodeli.core.infraestructura.persistencia.entidades.ProductoJpaEntity;
import com.psicodeli.core.infraestructura.persistencia.repositorios.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GestionProductoService {

    private final ProductoRepository productoRepository;

    public GestionProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public String generarSiguienteCodigo(CategoriaProducto categoria) {
        CategoriaProducto cat = (categoria != null) ? categoria : CategoriaProducto.CERVEZA;
        String prefijo = cat.getPrefijo() + "-";
        List<String> codigosExistentes = productoRepository.findCodigosByPrefijo(prefijo);

        int maxCorrelativo = 0;
        for (String c : codigosExistentes) {
            if (c != null && c.startsWith(prefijo)) {
                String sub = c.substring(prefijo.length()).trim();
                try {
                    int val = Integer.parseInt(sub);
                    if (val > maxCorrelativo) {
                        maxCorrelativo = val;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignora si el formato no es puramente numérico
                }
            }
        }
        return ProductoFunciones.generarCodigo(cat, maxCorrelativo + 1);
    }

    public Result<Producto, ErrorDominio> registrarProducto(
            String codigo,
            String nombre,
            String marca,
            CategoriaProducto categoria,
            Presentacion presentacion,
            BigDecimal precio,
            Integer stockInicial,
            Optional<DetalleCigarrillo> detalleCigarrillo) {

        String codigoFinal = (codigo != null && !codigo.isBlank())
                ? codigo.trim().toUpperCase()
                : generarSiguienteCodigo(categoria);

        Result<Producto, ErrorDominio> result = ProductoFunciones.crearProducto(
                codigoFinal, nombre, marca, categoria, presentacion, precio, stockInicial, detalleCigarrillo
        );

        if (result.isError()) {
            return result;
        }

        Producto producto = result.getValue();
        ProductoJpaEntity entity = ProductoJpaEntity.fromDomain(producto);
        ProductoJpaEntity guardada = productoRepository.save(entity);

        return Result.ok(guardada.toDomain());
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll().stream()
                .map(ProductoJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    public Result<Producto, ErrorDominio> cambiarEstado(UUID id, EstadoProducto nuevoEstado) {
        Optional<ProductoJpaEntity> opt = productoRepository.findById(id);
        if (opt.isEmpty()) {
            return Result.error(new ErrorDominio.ProductoNoEncontrado());
        }

        Producto dom = opt.get().toDomain();
        Producto modificado = ProductoFunciones.cambiarEstado(dom, nuevoEstado);
        ProductoJpaEntity guardada = productoRepository.save(ProductoJpaEntity.fromDomain(modificado));

        return Result.ok(guardada.toDomain());
    }

    public Result<Producto, ErrorDominio> abrirCajetilla(UUID id, int cantidadCajetillas) {
        Optional<ProductoJpaEntity> opt = productoRepository.findById(id);
        if (opt.isEmpty()) {
            return Result.error(new ErrorDominio.ProductoNoEncontrado());
        }

        Producto dom = opt.get().toDomain();
        Result<Producto, ErrorDominio> res = ProductoFunciones.abrirCajetilla(dom, cantidadCajetillas);

        if (res.isError()) {
            return res;
        }

        Producto actualizando = res.getValue();
        ProductoJpaEntity guardada = productoRepository.save(ProductoJpaEntity.fromDomain(actualizando));

        return Result.ok(guardada.toDomain());
    }
}
