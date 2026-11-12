package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "formulas")
public class Formula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCreacion;
    private String nombre;
    private Boolean estadoActivo;

    @OneToMany(
        mappedBy = "formula",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<FormulaMedicamento> medicamentos = new ArrayList<>();

    public Formula() {
    }

    public Formula(Integer id,
                   LocalDateTime fechaActualizacion,
                   LocalDateTime fechaCreacion,
                   String nombre,
                   Boolean estadoActivo) {

        this.id = id;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaCreacion = fechaCreacion;
        this.nombre = nombre;
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

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    public List<FormulaMedicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<FormulaMedicamento> medicamentos) {
        this.medicamentos.clear();

        if (medicamentos != null) {
            for (FormulaMedicamento relacion : medicamentos) {
                agregarRelacion(relacion);
            }
        }
    }

    public void agregarRelacion(FormulaMedicamento relacion) {
        if (relacion != null) {
            relacion.setFormula(this);
            this.medicamentos.add(relacion);
        }
    }

    public void agregarMedicamento(Medicamento medicamento) {
        if (medicamento != null) {
            FormulaMedicamento relacion =
                    new FormulaMedicamento(this, medicamento);

            this.medicamentos.add(relacion);
        }
    }

    public void eliminarMedicamento(FormulaMedicamento relacion) {
        if (relacion != null) {
            this.medicamentos.remove(relacion);
        }
    }

    @Override
    public String toString() {
        return "Formula{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estadoActivo=" + estadoActivo +
                '}';
    }
}
