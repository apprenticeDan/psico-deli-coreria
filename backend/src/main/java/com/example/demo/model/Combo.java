package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "combos")
@Data
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;
    private Double porcentajeDescuento = 0.0; // Ej: 10.0 para un 10% de descuento
    private Double precioOriginal = 0.0;
    private Double precioFinal = 0.0;
    private Boolean activo = true;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "combo", orphanRemoval = true)
    private List<DetalleCombo> productosCombo;
}