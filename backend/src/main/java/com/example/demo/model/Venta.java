package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario trabajador; // Identifica qué trabajador realizó la venta

    private Double totalVenta;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venta")
    private List<DetalleVenta> detalles;
}