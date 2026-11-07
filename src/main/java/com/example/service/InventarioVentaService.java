package com.example.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.DetalleVenta;
import com.example.model.Inventario;
import com.example.model.Medicamento;
import com.example.model.Venta;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class InventarioVentaService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Registra un detalle de venta y descuenta las unidades del lote
     * vigente con fecha de vencimiento más próxima que tenga stock suficiente.
     */
    public DetalleVenta registrarDetalle(DetalleVenta detalle) {

        validarDetalle(detalle);

        Inventario inventario = buscarLoteDisponible(
                detalle.getMedicamento().getId(),
                detalle.getCantidad()
        );

        descontar(inventario, detalle.getCantidad());

        detalle.setInventario(inventario);
        entityManager.persist(detalle);

        return detalle;
    }

    /**
     * Actualiza un detalle: devuelve primero las unidades anteriores
     * al lote original y luego descuenta la nueva cantidad del lote
     * correspondiente al medicamento seleccionado.
     *
     * Si alguna validación falla, la transacción revierte también
     * la devolución temporal de las unidades anteriores.
     */
    public DetalleVenta actualizarDetalle(DetalleVenta datos) {

        if (datos == null || datos.getId() == null) {
            throw new IllegalArgumentException(
                    "No se recibió un detalle de venta válido para actualizar."
            );
        }

        validarDetalle(datos);

        DetalleVenta actual = entityManager.find(
                DetalleVenta.class,
                datos.getId(),
                LockModeType.PESSIMISTIC_WRITE
        );

        if (actual == null) {
            throw new IllegalArgumentException(
                    "El detalle de venta ya no existe."
            );
        }

        Inventario inventarioAnterior = actual.getInventario();

        if (inventarioAnterior != null) {
            Inventario loteAnterior = entityManager.find(
                    Inventario.class,
                    inventarioAnterior.getId(),
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (loteAnterior != null) {
                loteAnterior.setStock(
                        valorStock(loteAnterior) + valorCantidad(actual)
                );
                loteAnterior.setFechaActualizacion(LocalDateTime.now());
            }
        }

        Inventario nuevoInventario = buscarLoteDisponible(
                datos.getMedicamento().getId(),
                datos.getCantidad()
        );

        descontar(nuevoInventario, datos.getCantidad());

        actual.setCantidad(datos.getCantidad());
        actual.setSubtotal(datos.getSubtotal());
        actual.setMedicamento(entityManager.getReference(
                Medicamento.class,
                datos.getMedicamento().getId()
        ));
        actual.setVenta(entityManager.getReference(
                Venta.class,
                datos.getVenta().getId()
        ));
        actual.setEstadoActivo(datos.getEstadoActivo());
        actual.setFechaActualizacion(LocalDateTime.now());
        actual.setInventario(nuevoInventario);

        return actual;
    }

    /**
     * Elimina un detalle y devuelve sus unidades al lote del que salieron.
     */
    public void eliminarDetalle(Integer detalleId) {

        if (detalleId == null) {
            throw new IllegalArgumentException(
                    "Selecciona un detalle de venta."
            );
        }

        DetalleVenta detalle = entityManager.find(
                DetalleVenta.class,
                detalleId,
                LockModeType.PESSIMISTIC_WRITE
        );

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "El detalle de venta ya no existe."
            );
        }

        Inventario referenciaInventario = detalle.getInventario();

        if (referenciaInventario != null) {
            Inventario inventario = entityManager.find(
                    Inventario.class,
                    referenciaInventario.getId(),
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (inventario != null) {
                inventario.setStock(
                        valorStock(inventario) + valorCantidad(detalle)
                );
                inventario.setFechaActualizacion(LocalDateTime.now());
            }
        }

        entityManager.remove(detalle);
    }

    /**
     * Elimina una venta completa y devuelve al inventario las unidades
     * asociadas a cada detalle antes de que opere el borrado en cascada.
     */
    public void eliminarVentaCompleta(Integer ventaId) {

        if (ventaId == null) {
            throw new IllegalArgumentException("Selecciona una venta.");
        }

        Venta venta = entityManager.createQuery(
                "SELECT DISTINCT v FROM Venta v "
                + "LEFT JOIN FETCH v.detalles d "
                + "LEFT JOIN FETCH d.inventario "
                + "WHERE v.id = :ventaId",
                Venta.class
        )
        .setParameter("ventaId", ventaId)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
                "La venta ya no existe."
        ));

        for (DetalleVenta detalle : venta.getDetalles()) {
            Inventario referencia = detalle.getInventario();
            if (referencia == null) {
                continue;
            }

            Inventario inventario = entityManager.find(
                    Inventario.class,
                    referencia.getId(),
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (inventario != null) {
                inventario.setStock(
                        valorStock(inventario) + valorCantidad(detalle)
                );
                inventario.setFechaActualizacion(LocalDateTime.now());
            }
        }

        entityManager.remove(venta);
    }

    private void validarDetalle(DetalleVenta detalle) {

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "No se recibió información del detalle de venta."
            );
        }

        if (detalle.getMedicamento() == null
                || detalle.getMedicamento().getId() == null) {
            throw new IllegalArgumentException(
                    "Selecciona un medicamento para el detalle."
            );
        }

        if (detalle.getVenta() == null
                || detalle.getVenta().getId() == null) {
            throw new IllegalArgumentException(
                    "Selecciona una venta para el detalle."
            );
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad vendida debe ser mayor que cero."
            );
        }

        if (detalle.getSubtotal() == null || detalle.getSubtotal() < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no puede ser negativo."
            );
        }
    }

    /**
     * Selecciona un único lote activo, no vencido y con unidades suficientes.
     * Se bloquea la fila para evitar que dos ventas descuenten simultáneamente
     * el mismo stock disponible.
     */
    private Inventario buscarLoteDisponible(
            Integer medicamentoId,
            Integer cantidad) {

        LocalDateTime ahora = LocalDateTime.now();

        return entityManager.createQuery(
                "SELECT i FROM Inventario i "
                + "WHERE i.medicamento.id = :medicamentoId "
                + "AND i.estadoActivo = true "
                + "AND i.stock >= :cantidad "
                + "AND i.fechaVencimiento >= :ahora "
                + "ORDER BY i.fechaVencimiento ASC, i.id ASC",
                Inventario.class
        )
        .setParameter("medicamentoId", medicamentoId)
        .setParameter("cantidad", cantidad)
        .setParameter("ahora", ahora)
        .setMaxResults(1)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
                "No hay un lote activo y vigente con stock suficiente "
                + "para la cantidad solicitada. Revisa las existencias."
        ));
    }

    private void descontar(Inventario inventario, Integer cantidad) {

        int stockActual = valorStock(inventario);

        if (stockActual < cantidad) {
            throw new IllegalStateException(
                    "El inventario no tiene existencias suficientes."
            );
        }

        inventario.setStock(stockActual - cantidad);
        inventario.setFechaActualizacion(LocalDateTime.now());
    }

    private int valorStock(Inventario inventario) {
        return inventario.getStock() != null
                ? inventario.getStock()
                : 0;
    }

    private int valorCantidad(DetalleVenta detalle) {
        return detalle.getCantidad() != null
                ? detalle.getCantidad()
                : 0;
    }
}
