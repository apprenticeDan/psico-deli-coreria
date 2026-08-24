package com.psicodeli.core.infraestructura.persistencia.entidades;

import com.psicodeli.core.dominio.usuario.EstadoTrabajador;
import com.psicodeli.core.dominio.usuario.Rol;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "trabajadores")
public class TrabajadorJpaEntity {

    @Id
    private UUID id;
    private String nombreCompleto;
    private String usuario;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    private EstadoTrabajador estado;

    // Horario Asignado
    private String horarioDias; // CSV de dias
    private LocalTime horarioHoraInicio;
    private LocalTime horarioHoraFin;

    public TrabajadorJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public EstadoTrabajador getEstado() { return estado; }
    public void setEstado(EstadoTrabajador estado) { this.estado = estado; }

    public String getHorarioDias() { return horarioDias; }
    public void setHorarioDias(String horarioDias) { this.horarioDias = horarioDias; }

    public LocalTime getHorarioHoraInicio() { return horarioHoraInicio; }
    public void setHorarioHoraInicio(LocalTime horarioHoraInicio) { this.horarioHoraInicio = horarioHoraInicio; }

    public LocalTime getHorarioHoraFin() { return horarioHoraFin; }
    public void setHorarioHoraFin(LocalTime horarioHoraFin) { this.horarioHoraFin = horarioHoraFin; }
}
