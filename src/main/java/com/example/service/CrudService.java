
package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Inventario;
import com.example.model.Laboratorio;
import com.example.model.Medicamento;
import com.example.model.Venta;
import com.example.model.DetalleVenta;
import com.example.model.Formula;
import com.example.model.FormulaMedicamento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class CrudService {

    @PersistenceContext
    private EntityManager entityManager;

    // REGISTRAR
    public <T> T guardar(T entidad) {
        entityManager.persist(entidad);
        return entidad;
    }

    // BUSCAR POR ID
    @Transactional(readOnly = true)
    public <T> T buscarPorId(Class<T> clase, Object id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(clase, id);
    }

    // LISTAR TODOS
    @Transactional(readOnly = true)
    public <T> List<T> listarTodos(Class<T> clase) {
        String jpql = "SELECT e FROM " + clase.getSimpleName() + " e";

        return entityManager
                .createQuery(jpql, clase)
                .getResultList();
    }

    // LISTAR LABORATORIOS
    @Transactional(readOnly = true)
    public List<Laboratorio> listarLaboratorios() {
        return entityManager
                .createQuery(
                    "SELECT l FROM Laboratorio l ORDER BY l.nombre",
                    Laboratorio.class
                )
                .getResultList();
    }

    // LISTAR MEDICAMENTOS CON SU LABORATORIO
    @Transactional(readOnly = true)
    public List<Medicamento> listarMedicamentosConLaboratorio() {
        return entityManager
                .createQuery(
                    "SELECT m FROM Medicamento m " +
                    "LEFT JOIN FETCH m.laboratorio " +
                    "ORDER BY m.nombre",
                    Medicamento.class
                )
                .getResultList();
    }

    // LISTAR INVENTARIOS CON MEDICAMENTO Y LABORATORIO
    @Transactional(readOnly = true)
    public List<Inventario> listarInventariosConMedicamento() {
        return entityManager
                .createQuery(
                    "SELECT i FROM Inventario i " +
                    "LEFT JOIN FETCH i.medicamento m " +
                    "LEFT JOIN FETCH m.laboratorio " +
                    "ORDER BY i.id",
                    Inventario.class
                )
                .getResultList();
    }

    // ACTUALIZAR
    public <T> T actualizar(T entidad) {
        return entityManager.merge(entidad);
    }

    // ELIMINAR
    public <T> boolean eliminar(Class<T> clase, Object id) {
        if (id == null) {
            return false;
        }

        T entidad = entityManager.find(clase, id);

        if (entidad != null) {
            entityManager.remove(entidad);
            return true;
        }

        return false;
    }

    // VERIFICAR EXISTENCIA
    @Transactional(readOnly = true)
    public <T> boolean existe(Class<T> clase, Object id) {
        if (id == null) {
            return false;
        }

        return entityManager.find(clase, id) != null;
    }

    // =====================================
    // VENTAS CON SUS DETALLES Y MEDICAMENTOS
    // =====================================

    @Transactional(readOnly = true)
    public List<Venta> listarVentasConDetalles() {

        return entityManager.createQuery(
                "SELECT DISTINCT v FROM Venta v " +
                "LEFT JOIN FETCH v.detalles d " +
                "LEFT JOIN FETCH d.medicamento " +
                "ORDER BY v.id DESC",
                Venta.class
        ).getResultList();
    }

    // =====================================
    // DETALLES DE VENTA
    // =====================================

    @Transactional(readOnly = true)
    public List<DetalleVenta> listarDetallesVenta() {

        return entityManager.createQuery(
                "SELECT d FROM DetalleVenta d " +
                "LEFT JOIN FETCH d.medicamento " +
                "LEFT JOIN FETCH d.venta " +
                "ORDER BY d.id",
                DetalleVenta.class
        ).getResultList();
    }

    // =====================================
    // FÓRMULAS CON SUS MEDICAMENTOS
    // =====================================

    @Transactional(readOnly = true)
    public List<Formula> listarFormulasConMedicamentos() {

        return entityManager.createQuery(
                "SELECT DISTINCT f FROM Formula f " +
                "LEFT JOIN FETCH f.medicamentos fm " +
                "LEFT JOIN FETCH fm.medicamento " +
                "ORDER BY f.id",
                Formula.class
        ).getResultList();
    }

    // =====================================
    // RELACIONES FÓRMULA-MEDICAMENTO
    // =====================================

    @Transactional(readOnly = true)
    public List<FormulaMedicamento> listarRelacionesFormulaMedicamento() {

        return entityManager.createQuery(
                "SELECT fm FROM FormulaMedicamento fm " +
                "JOIN FETCH fm.formula " +
                "JOIN FETCH fm.medicamento",
                FormulaMedicamento.class
        ).getResultList();
    }
}
