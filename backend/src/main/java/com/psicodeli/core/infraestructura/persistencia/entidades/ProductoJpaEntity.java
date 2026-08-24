package com.psicodeli.core.infraestructura.persistencia.entidades;

import com.psicodeli.core.dominio.producto.CategoriaProducto;
import com.psicodeli.core.dominio.producto.EstadoProducto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class ProductoJpaEntity {

    @Id
    private UUID id;
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    private CategoriaProducto categoria;
    
    private BigDecimal precio;
    
    @Enumerated(EnumType.STRING)
    private EstadoProducto estado;
    
    private Integer stock;
    private BigDecimal descuento;

    public ProductoJpaEntity() {
    }

    // Getters and Setters omitted for brevity but required by JPA
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public EstadoProducto getEstado() { return estado; }
    public void setEstado(EstadoProducto estado) { this.estado = estado; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}
