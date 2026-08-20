package com.example.demo.service;

import com.example.demo.model.DetalleVenta;
import com.example.demo.model.Producto;
import com.example.demo.model.Venta;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Venta registrarVenta(Venta venta) {
        double totalVenta = 0.0;

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe contener al menos un producto.");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar stock insuficiente (Integridad de datos)
            if (producto.getStockDisponible() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() +
                        " (Stock disponible: " + producto.getStockDisponible() + ")");
            }

            // Actualizar inventario automáticamente
            producto.setStockDisponible(producto.getStockDisponible() - detalle.getCantidad());
            productoRepository.save(producto);

            // Calcular precio según si es venta de cajetilla completa o cigarrillos por unidad
            double precioUnitario;
            if (Boolean.TRUE.equals(detalle.getEsVentaPorUnidadCigarrillo())) {
                if (producto.getPrecioPorUnidadCigarrillo() == null) {
                    throw new RuntimeException("El producto " + producto.getNombre() + " no tiene calculado el precio por unidad.");
                }
                precioUnitario = producto.getPrecioPorUnidadCigarrillo();
            } else {
                precioUnitario = producto.getPrecioVenta();
            }

            double subtotal = precioUnitario * detalle.getCantidad();
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);

            totalVenta += subtotal;
        }

        venta.setTotalVenta(totalVenta);
        return ventaRepository.save(venta);
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }
}