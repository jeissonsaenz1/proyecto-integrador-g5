
package com.example.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicamentos")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCreacion;

    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    private Boolean estadoActivo;

    public Medicamento() {
    }

    public Medicamento(Integer id,
                       LocalDateTime fechaActualizacion,
                       LocalDateTime fechaCreacion,
                       String nombre,
                       Laboratorio laboratorio,
                       Boolean estadoActivo) {

        this.id = id;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaCreacion = fechaCreacion;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.estadoActivo = estadoActivo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", laboratorio=" +
                (laboratorio != null ? laboratorio.getId() : null) +
                ", estadoActivo=" + estadoActivo +
                '}';
    }
}
