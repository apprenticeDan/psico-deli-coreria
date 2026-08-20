package com.example.demo.service;

import com.example.demo.model.Combo;
import com.example.demo.model.DetalleCombo;
import com.example.demo.model.Producto;
import com.example.demo.repository.ComboRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ComboService {

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Combo guardarCombo(Combo combo) {
        double precioOriginal = 0.0;

        if (combo.getProductosCombo() != null) {
            for (DetalleCombo detalle : combo.getProductosCombo()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado para agregar al combo"));

                detalle.setCombo(combo);
                // Sumar precio original: Precio de venta del producto por la cantidad en el combo
                precioOriginal += producto.getPrecioVenta() * detalle.getCantidad();
            }
        }

        combo.setPrecioOriginal(precioOriginal);

        // Aplicar automáticamente el descuento y calcular precio final
        double descuento = combo.getPorcentajeDescuento() != null ? combo.getPorcentajeDescuento() : 0.0;
        double precioFinal = precioOriginal - (precioOriginal * (descuento / 100.0));

        // Redondear a 2 decimales
        combo.setPrecioFinal(Math.round(precioFinal * 100.0) / 100.0);

        return comboRepository.save(combo);
    }

    public List<Combo> listarCombos() {
        return comboRepository.findAll();
    }

    public Optional<Combo> buscarPorId(Long id) {
        return comboRepository.findById(id);
    }

    public void eliminarCombo(Long id) {
        comboRepository.deleteById(id);
    }
}