package com.psicodeli.core.infraestructura.web;

import com.psicodeli.core.aplicacion.producto.GestionProductoService;
import com.psicodeli.core.dominio.compartido.ErrorDominio;
import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.producto.*;
import com.psicodeli.core.infraestructura.web.dto.ProductoRequest;
import com.psicodeli.core.infraestructura.web.dto.ProductoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductoController {

    private final GestionProductoService gestionService;

    public ProductoController(GestionProductoService gestionService) {
        this.gestionService = gestionService;
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        List<ProductoResponse> response = gestionService.listarProductos().stream()
                .map(ProductoResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/productos")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequest request) {
        Presentacion presentacion = new Presentacion(
                request.empaque(),
                request.contenido(),
                request.unidad()
        );

        Optional<DetalleCigarrillo> detalleCigarrillo = Optional.empty();
        if (Boolean.TRUE.equals(request.esCigarrillo()) || request.categoria() == CategoriaProducto.CIGARRILLO) {
            detalleCigarrillo = Optional.of(new DetalleCigarrillo(
                    request.unidadesPorCajetilla() != null ? request.unidadesPorCajetilla() : 20,
                    0,
                    request.precioUnidadSuelta()
            ));
        }

        Result<Producto, ErrorDominio> result = gestionService.registrarProducto(
                request.codigo(),
                request.nombre(),
                request.marca(),
                request.categoria(),
                presentacion,
                request.precio(),
                request.stock(),
                detalleCigarrillo
        );

        if (result.isOk()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ProductoResponse.fromDomain(result.getValue()));
        } else {
            return ResponseEntity.badRequest().body(result.getError().getClass().getSimpleName());
        }
    }

    @PatchMapping("/productos/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        String estadoStr = request.get("estado");
        if (estadoStr == null || estadoStr.isBlank()) {
            return ResponseEntity.badRequest().body("El campo 'estado' es requerido");
        }

        try {
            EstadoProducto nuevoEstado = EstadoProducto.valueOf(estadoStr.toUpperCase());
            Result<Producto, ErrorDominio> result = gestionService.cambiarEstado(id, nuevoEstado);

            if (result.isOk()) {
                return ResponseEntity.ok(ProductoResponse.fromDomain(result.getValue()));
            } else {
                return ResponseEntity.badRequest().body(result.getError().getClass().getSimpleName());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido. Usar ACTIVO o INACTIVO");
        }
    }

    @PostMapping("/productos/{id}/abrir-cajetilla")
    public ResponseEntity<?> abrirCajetilla(@PathVariable UUID id, @RequestBody Map<String, Integer> request) {
        Integer cantidad = request.getOrDefault("cantidad", 1);
        Result<Producto, ErrorDominio> result = gestionService.abrirCajetilla(id, cantidad);

        if (result.isOk()) {
            return ResponseEntity.ok(ProductoResponse.fromDomain(result.getValue()));
        } else {
            return ResponseEntity.badRequest().body(result.getError().getClass().getSimpleName());
        }
    }
}
