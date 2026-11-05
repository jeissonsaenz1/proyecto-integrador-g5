
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
@Table(name = "inventarios")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaVencimiento;

    private String lote;
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    private Boolean estadoActivo;

    public Inventario() {
    }

    public Inventario(Integer id,
                      LocalDateTime fechaActualizacion,
                      LocalDateTime fechaCreacion,
                      LocalDateTime fechaVencimiento,
                      String lote,
                      Integer stock,
                      Medicamento medicamento,
                      Boolean estadoActivo) {

        this.id = id;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaCreacion = fechaCreacion;
        this.fechaVencimiento = fechaVencimiento;
        this.lote = lote;
        this.stock = stock;
        this.medicamento = medicamento;
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

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    @Override
    public String toString() {
        return "Inventario{" +
                "id=" + id +
                ", fechaVencimiento=" + fechaVencimiento +
                ", lote='" + lote + '\'' +
                ", stock=" + stock +
                ", medicamentoId=" +
                (medicamento != null ? medicamento.getId() : null) +
                ", estadoActivo=" + estadoActivo +
                '}';
    }
}
