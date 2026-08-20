package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String codigo;

    private String categoria; // CERVEZAS, GASEOSAS, CIGARRILLOS, REFRESCOS, TRAGOS, COMBOS
    private String marca;
    private String presentacion; // Botella, Lata, Cajetilla, etc.

    private Double precioVenta;
    private Double costo;
    private Integer stockDisponible; // Cantidad en unidades o cajetillas
    private Boolean activo = true;

    // --- Gestión especial para Cigarrillos ---
    private Boolean esCigarrillo = false;
    private Integer unidadesPorCajetilla; // Ej: 20 cigarrillos por cajetilla
    private Double precioPorUnidadCigarrillo;
    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;
    // Precio calculado por unidad suelta
}