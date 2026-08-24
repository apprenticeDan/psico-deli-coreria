package com.psicodeli.core.infraestructura.persistencia.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sesiones")
public class SesionJpaEntity {

    @Id
    private UUID id;
    private UUID trabajadorId;
    private LocalDateTime inicio;
    private LocalDateTime fin;

    public SesionJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTrabajadorId() { return trabajadorId; }
    public void setTrabajadorId(UUID trabajadorId) { this.trabajadorId = trabajadorId; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
}
