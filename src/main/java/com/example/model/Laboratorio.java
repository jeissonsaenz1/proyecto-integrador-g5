
package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "laboratorios")
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCreacion;
    private String direccion;
    private String ciudad;
    private String pais;
    private String nombre;
    private Boolean estadoActivo;

    @OneToMany(
        mappedBy = "laboratorio",
        fetch = FetchType.LAZY
    )
    private List<Medicamento> medicamentos = new ArrayList<>();

    public Laboratorio() {
    }

    public Laboratorio(Integer id,
                       LocalDateTime fechaActualizacion,
                       LocalDateTime fechaCreacion,
                       String direccion,
                       String ciudad,
                       String pais,
                       String nombre,
                       Boolean estadoActivo) {

        this.id = id;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaCreacion = fechaCreacion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos != null
                ? medicamentos
                : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Laboratorio{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", pais='" + pais + '\'' +
                ", estadoActivo=" + estadoActivo +
                '}';
    }
}
