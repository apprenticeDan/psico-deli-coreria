package com.psicodeli.core.infraestructura.persistencia.entidades;

import com.psicodeli.core.dominio.producto.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class ProductoJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigo;

    private String nombre;
    private String marca;

    @Enumerated(EnumType.STRING)
    private CategoriaProducto categoria;

    private String presentacionEmpaque;
    private BigDecimal presentacionContenido;

    @Enumerated(EnumType.STRING)
    private UnidadMedida presentacionUnidad;

    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    private EstadoProducto estado;

    private Integer stock;
    private BigDecimal descuento;

    // Campos específicos para Cigarrillos
    private Boolean esCigarrillo = false;
    private Integer unidadesPorCajetilla;
    private Integer stockUnidadesSueltas;
    private BigDecimal precioUnidadSuelta;

    public ProductoJpaEntity() {}

    public static ProductoJpaEntity fromDomain(Producto p) {
        ProductoJpaEntity entity = new ProductoJpaEntity();
        entity.setId(p.id());
        entity.setCodigo(p.codigo());
        entity.setNombre(p.nombre());
        entity.setMarca(p.marca());
        entity.setCategoria(p.categoria());
        
        if (p.presentacion() != null) {
            entity.setPresentacionEmpaque(p.presentacion().empaque());
            entity.setPresentacionContenido(p.presentacion().contenido());
            entity.setPresentacionUnidad(p.presentacion().unidad());
        }

        entity.setPrecio(p.precio());
        entity.setEstado(p.estado());
        entity.setStock(p.stock());
        entity.setDescuento(p.descuento().orElse(null));

        if (p.detalleCigarrillo().isPresent()) {
            DetalleCigarrillo det = p.detalleCigarrillo().get();
            entity.setEsCigarrillo(true);
            entity.setUnidadesPorCajetilla(det.unidadesPorCajetilla());
            entity.setStockUnidadesSueltas(det.stockUnidadesSueltas());
            entity.setPrecioUnidadSuelta(det.precioUnidadSuelta());
        } else {
            entity.setEsCigarrillo(false);
            entity.setUnidadesPorCajetilla(null);
            entity.setStockUnidadesSueltas(null);
            entity.setPrecioUnidadSuelta(null);
        }

        return entity;
    }

    public Producto toDomain() {
        Presentacion pres = new Presentacion(
                presentacionEmpaque != null ? presentacionEmpaque : "Unidad",
                presentacionContenido != null ? presentacionContenido : BigDecimal.ONE,
                presentacionUnidad != null ? presentacionUnidad : UnidadMedida.UNIDAD
        );

        Optional<DetalleCigarrillo> detCigarrillo = Optional.empty();
        if (Boolean.TRUE.equals(esCigarrillo) || categoria == CategoriaProducto.CIGARRILLO) {
            detCigarrillo = Optional.of(new DetalleCigarrillo(
                    unidadesPorCajetilla != null ? unidadesPorCajetilla : 20,
                    stockUnidadesSueltas != null ? stockUnidadesSueltas : 0,
                    precioUnidadSuelta != null ? precioUnidadSuelta : BigDecimal.ZERO
            ));
        }

        return new Producto(
                id,
                codigo != null ? codigo : "PROD-" + id,
                nombre,
                marca != null ? marca : "",
                categoria != null ? categoria : CategoriaProducto.CERVEZA,
                pres,
                precio != null ? precio : BigDecimal.ZERO,
                estado != null ? estado : EstadoProducto.ACTIVO,
                stock != null ? stock : 0,
                Optional.ofNullable(descuento),
                detCigarrillo
        );
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }
    public String getPresentacionEmpaque() { return presentacionEmpaque; }
    public void setPresentacionEmpaque(String presentacionEmpaque) { this.presentacionEmpaque = presentacionEmpaque; }
    public BigDecimal getPresentacionContenido() { return presentacionContenido; }
    public void setPresentacionContenido(BigDecimal presentacionContenido) { this.presentacionContenido = presentacionContenido; }
    public UnidadMedida getPresentacionUnidad() { return presentacionUnidad; }
    public void setPresentacionUnidad(UnidadMedida presentacionUnidad) { this.presentacionUnidad = presentacionUnidad; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public EstadoProducto getEstado() { return estado; }
    public void setEstado(EstadoProducto estado) { this.estado = estado; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public Boolean getEsCigarrillo() { return esCigarrillo; }
    public void setEsCigarrillo(Boolean esCigarrillo) { this.esCigarrillo = esCigarrillo; }
    public Integer getUnidadesPorCajetilla() { return unidadesPorCajetilla; }
    public void setUnidadesPorCajetilla(Integer unidadesPorCajetilla) { this.unidadesPorCajetilla = unidadesPorCajetilla; }
    public Integer getStockUnidadesSueltas() { return stockUnidadesSueltas; }
    public void setStockUnidadesSueltas(Integer stockUnidadesSueltas) { this.stockUnidadesSueltas = stockUnidadesSueltas; }
    public BigDecimal getPrecioUnidadSuelta() { return precioUnidadSuelta; }
    public void setPrecioUnidadSuelta(BigDecimal precioUnidadSuelta) { this.precioUnidadSuelta = precioUnidadSuelta; }
}
