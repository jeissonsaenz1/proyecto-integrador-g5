package com.example.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "formulas_medicamentos")
@IdClass(FormulaMedicamento.FormulaMedicamentoId.class)
public class FormulaMedicamento {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id", nullable = false)
    private Formula formula;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    public FormulaMedicamento() {
    }

    public FormulaMedicamento(Formula formula, Medicamento medicamento) {
        this.formula = formula;
        this.medicamento = medicamento;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Integer getFormulaId() {
        return formula != null ? formula.getId() : null;
    }

    public Integer getMedicamentoId() {
        return medicamento != null ? medicamento.getId() : null;
    }

    @Override
    public String toString() {
        return "FormulaMedicamento{" +
                "formulaId=" + getFormulaId() +
                ", medicamentoId=" + getMedicamentoId() +
                '}';
    }

    public static class FormulaMedicamentoId implements Serializable {

        private Integer formula;
        private Integer medicamento;

        public FormulaMedicamentoId() {
        }

        public FormulaMedicamentoId(Integer formula, Integer medicamento) {
            this.formula = formula;
            this.medicamento = medicamento;
        }

        public Integer getFormula() {
            return formula;
        }

        public void setFormula(Integer formula) {
            this.formula = formula;
        }

        public Integer getMedicamento() {
            return medicamento;
        }

        public void setMedicamento(Integer medicamento) {
            this.medicamento = medicamento;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (!(object instanceof FormulaMedicamentoId)) {
                return false;
            }

            FormulaMedicamentoId that =
                    (FormulaMedicamentoId) object;

            return Objects.equals(formula, that.formula)
                    && Objects.equals(medicamento, that.medicamento);
        }

        @Override
        public int hashCode() {
            return Objects.hash(formula, medicamento);
        }
    }
}