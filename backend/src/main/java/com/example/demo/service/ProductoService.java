package com.example.demo.service;

import com.example.demo.model.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public Producto guardarProducto(Producto producto) {
        // Lógica especial para cigarrillos: si es cigarrillo y tiene unidades por cajetilla
        if (Boolean.TRUE.equals(producto.getEsCigarrillo()) && producto.getUnidadesPorCajetilla() != null && producto.getUnidadesPorCajetilla() > 0) {
            if (producto.getPrecioVenta() != null) {
                // Calcular automáticamente el precio por unidad suelta
                double precioUnitario = producto.getPrecioVenta() / producto.getUnidadesPorCajetilla();
                // Redondear a 2 decimales opcionalmente o asignarlo directo
                producto.setPrecioPorUnidadCigarrillo(Math.round(precioUnitario * 100.0) / 100.0);
            }
        }
        return productoRepository.save(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}