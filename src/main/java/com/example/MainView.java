
package com.example;

import java.time.LocalDateTime;

import com.example.model.DetalleVenta;
import com.example.model.Formula;
import com.example.model.FormulaMedicamento;
import com.example.model.Inventario;
import com.example.model.Laboratorio;
import com.example.model.Medicamento;
import com.example.model.Venta;
import com.example.service.CrudService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("FarmaCare")
@Route("")
public class MainView extends VerticalLayout {

    private final CrudService crudService;

    // =====================================
    // LABORATORIOS
    // =====================================

    private final Grid<Laboratorio> gridLaboratorios =
            new Grid<>(Laboratorio.class, false);

    private final TextField nombreLaboratorio = new TextField("Nombre");
    private final TextField direccionLaboratorio = new TextField("Dirección");
    private final TextField ciudadLaboratorio = new TextField("Ciudad");
    private final TextField paisLaboratorio = new TextField("País");
    private final Checkbox activoLaboratorio = new Checkbox("Activo");

    private Laboratorio laboratorioSeleccionado;

    // =====================================
    // MEDICAMENTOS
    // =====================================

    private final Grid<Medicamento> gridMedicamentos =
            new Grid<>(Medicamento.class, false);

    private final TextField nombreMedicamento =
            new TextField("Nombre del medicamento");

    private final ComboBox<Laboratorio> comboLaboratorio =
            new ComboBox<>("Laboratorio");

    private final Checkbox activoMedicamento = new Checkbox("Activo");

    private Medicamento medicamentoSeleccionado;

    // =====================================
    // INVENTARIOS
    // =====================================

    private final Grid<Inventario> gridInventarios =
            new Grid<>(Inventario.class, false);

    private final TextField loteInventario = new TextField("Número de lote");
    private final TextField stockInventario = new TextField("Cantidad disponible");

    private final DateTimePicker vencimientoInventario =
            new DateTimePicker("Fecha y hora de vencimiento");

    private final ComboBox<Medicamento> comboMedicamento =
            new ComboBox<>("Medicamento");

    private final Checkbox activoInventario = new Checkbox("Activo");

    private Inventario inventarioSeleccionado;

    // =====================================
    // VENTAS
    // =====================================

    private final Grid<Venta> gridVentas =
            new Grid<>(Venta.class, false);

    private final TextField estadoVenta =
            new TextField("Estado de la venta");

    private final TextField totalVenta =
            new TextField("Total");

    private final Checkbox activoVenta = new Checkbox("Activo");

    private Venta ventaSeleccionada;

    // =====================================
    // DETALLES DE VENTA
    // =====================================

    private final Grid<DetalleVenta> gridDetallesVenta =
            new Grid<>(DetalleVenta.class, false);

    private final ComboBox<Venta> comboVenta =
            new ComboBox<>("Venta");

    private final ComboBox<Medicamento> comboMedicamentoVenta =
            new ComboBox<>("Medicamento");

    private final TextField cantidadVenta =
            new TextField("Cantidad");

    private final TextField subtotalVenta =
            new TextField("Subtotal");

    private final Checkbox activoDetalleVenta = new Checkbox("Activo");

    private DetalleVenta detalleVentaSeleccionado;

    // =====================================
    // FORMULAS
    // =====================================

    private final Grid<Formula> gridFormulas =
            new Grid<>(Formula.class, false);

    private final TextField nombreFormula =
            new TextField("Nombre de la fórmula");

    private final Checkbox activoFormula = new Checkbox("Activo");

    private Formula formulaSeleccionada;

    // =====================================
    // FORMULA - MEDICAMENTO
    // =====================================

    private final Grid<FormulaMedicamento> gridFormulaMedicamentos =
            new Grid<>(FormulaMedicamento.class, false);

    private final ComboBox<Formula> comboFormula =
            new ComboBox<>("Fórmula");

    private final ComboBox<Medicamento> comboMedicamentoFormula =
            new ComboBox<>("Medicamento");

    private FormulaMedicamento relacionSeleccionada;

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public MainView(CrudService crudService) {

        this.crudService = crudService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Sistema de Gestión de Farmacia");

        TabSheet pestanas = new TabSheet();
        pestanas.setSizeFull();

        pestanas.add("Laboratorios", crearVistaLaboratorios());
        pestanas.add("Medicamentos", crearVistaMedicamentos());
        pestanas.add("Inventarios", crearVistaInventarios());
        pestanas.add("Ventas", crearVistaVentas());
        pestanas.add("Detalles de venta", crearVistaDetallesVenta());
        pestanas.add("Fórmulas", crearVistaFormulas());
        pestanas.add("Fórmula-Medicamento", crearVistaFormulaMedicamentos());

        add(titulo, pestanas);

        actualizarLaboratorios();
        actualizarMedicamentos();
        actualizarInventarios();
        actualizarVentas();
        actualizarDetallesVenta();
        actualizarFormulas();
        actualizarFormulaMedicamentos();
        actualizarCombos();
    }

    // =====================================
    // LABORATORIOS
    // =====================================

    private Component crearVistaLaboratorios() {

        gridLaboratorios.addColumn(Laboratorio::getId).setHeader("ID");
        gridLaboratorios.addColumn(Laboratorio::getNombre).setHeader("Nombre");
        gridLaboratorios.addColumn(Laboratorio::getDireccion).setHeader("Dirección");
        gridLaboratorios.addColumn(Laboratorio::getCiudad).setHeader("Ciudad");
        gridLaboratorios.addColumn(Laboratorio::getPais).setHeader("País");
        gridLaboratorios.addColumn(Laboratorio::getEstadoActivo).setHeader("Activo");

        gridLaboratorios.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(
                nombreLaboratorio,
                direccionLaboratorio,
                ciudadLaboratorio,
                paisLaboratorio,
                activoLaboratorio
        );

        Button guardar = new Button("Guardar", e -> guardarLaboratorio());
        Button limpiar = new Button("Limpiar", e -> limpiarLaboratorio());
        Button eliminar = new Button("Eliminar", e -> eliminarLaboratorio());

        gridLaboratorios.asSingleSelect().addValueChangeListener(event -> {
            laboratorioSeleccionado = event.getValue();

            if (laboratorioSeleccionado != null) {
                nombreLaboratorio.setValue(valor(laboratorioSeleccionado.getNombre()));
                direccionLaboratorio.setValue(valor(laboratorioSeleccionado.getDireccion()));
                ciudadLaboratorio.setValue(valor(laboratorioSeleccionado.getCiudad()));
                paisLaboratorio.setValue(valor(laboratorioSeleccionado.getPais()));

                activoLaboratorio.setValue(
                        Boolean.TRUE.equals(laboratorioSeleccionado.getEstadoActivo())
                );
            }
        });

        return new VerticalLayout(
                new H2("Gestión de laboratorios"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridLaboratorios
        );
    }

    private void guardarLaboratorio() {

        if (nombreLaboratorio.isEmpty()
                || direccionLaboratorio.isEmpty()
                || ciudadLaboratorio.isEmpty()
                || paisLaboratorio.isEmpty()) {

            Notification.show("Completa todos los campos del laboratorio.");
            return;
        }

        try {
            boolean esNuevo = laboratorioSeleccionado == null;

            Laboratorio laboratorio = esNuevo
                    ? new Laboratorio()
                    : laboratorioSeleccionado;

            LocalDateTime ahora = LocalDateTime.now();

            if (esNuevo) {
                laboratorio.setFechaCreacion(ahora);
            }

            laboratorio.setFechaActualizacion(ahora);
            laboratorio.setNombre(nombreLaboratorio.getValue());
            laboratorio.setDireccion(direccionLaboratorio.getValue());
            laboratorio.setCiudad(ciudadLaboratorio.getValue());
            laboratorio.setPais(paisLaboratorio.getValue());
            laboratorio.setEstadoActivo(activoLaboratorio.getValue());

            if (esNuevo) {
                crudService.guardar(laboratorio);
            } else {
                crudService.actualizar(laboratorio);
            }

            Notification.show("Laboratorio guardado correctamente.");
            limpiarLaboratorio();
            actualizarLaboratorios();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("Error al guardar laboratorio: " + ex.getMessage());
        }
    }

    private void limpiarLaboratorio() {
        laboratorioSeleccionado = null;
        nombreLaboratorio.clear();
        direccionLaboratorio.clear();
        ciudadLaboratorio.clear();
        paisLaboratorio.clear();
        activoLaboratorio.setValue(false);
        gridLaboratorios.deselectAll();
    }

    private void eliminarLaboratorio() {

        if (laboratorioSeleccionado == null) {
            Notification.show("Selecciona un laboratorio.");
            return;
        }

        try {
            crudService.eliminar(
                    Laboratorio.class,
                    laboratorioSeleccionado.getId()
            );

            Notification.show("Laboratorio eliminado.");
            limpiarLaboratorio();
            actualizarLaboratorios();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("No se pudo eliminar el laboratorio.");
        }
    }

    private void actualizarLaboratorios() {
        gridLaboratorios.setItems(crudService.listarLaboratorios());
    }

    // =====================================
    // MEDICAMENTOS
    // =====================================

    private Component crearVistaMedicamentos() {

        comboLaboratorio.setItemLabelGenerator(Laboratorio::getNombre);
        comboLaboratorio.setWidthFull();

        gridMedicamentos.addColumn(Medicamento::getId).setHeader("ID");
        gridMedicamentos.addColumn(Medicamento::getNombre).setHeader("Medicamento");

        gridMedicamentos.addColumn(
                m -> m.getLaboratorio() != null
                        ? m.getLaboratorio().getNombre()
                        : "Sin laboratorio"
        ).setHeader("Laboratorio");

        gridMedicamentos.addColumn(Medicamento::getEstadoActivo).setHeader("Activo");
        gridMedicamentos.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(nombreMedicamento, comboLaboratorio, activoMedicamento);

        Button guardar = new Button("Guardar", e -> guardarMedicamento());
        Button limpiar = new Button("Limpiar", e -> limpiarMedicamento());
        Button eliminar = new Button("Eliminar", e -> eliminarMedicamento());

        gridMedicamentos.asSingleSelect().addValueChangeListener(event -> {
            medicamentoSeleccionado = event.getValue();

            if (medicamentoSeleccionado != null) {
                nombreMedicamento.setValue(valor(medicamentoSeleccionado.getNombre()));
                comboLaboratorio.setValue(medicamentoSeleccionado.getLaboratorio());

                activoMedicamento.setValue(
                        Boolean.TRUE.equals(medicamentoSeleccionado.getEstadoActivo())
                );
            }
        });

        return new VerticalLayout(
                new H2("Gestión de medicamentos"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridMedicamentos
        );
    }

    private void guardarMedicamento() {

        if (nombreMedicamento.isEmpty() || comboLaboratorio.isEmpty()) {
            Notification.show("Completa el nombre y selecciona un laboratorio.");
            return;
        }

        try {
            boolean esNuevo = medicamentoSeleccionado == null;

            Medicamento medicamento = esNuevo
                    ? new Medicamento()
                    : medicamentoSeleccionado;

            LocalDateTime ahora = LocalDateTime.now();

            if (esNuevo) {
                medicamento.setFechaCreacion(ahora);
            }

            medicamento.setFechaActualizacion(ahora);
            medicamento.setNombre(nombreMedicamento.getValue());
            medicamento.setLaboratorio(comboLaboratorio.getValue());
            medicamento.setEstadoActivo(activoMedicamento.getValue());

            if (esNuevo) {
                crudService.guardar(medicamento);
            } else {
                crudService.actualizar(medicamento);
            }

            Notification.show("Medicamento guardado correctamente.");
            limpiarMedicamento();
            actualizarMedicamentos();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("Error al guardar medicamento: " + ex.getMessage());
        }
    }

    private void limpiarMedicamento() {
        medicamentoSeleccionado = null;
        nombreMedicamento.clear();
        comboLaboratorio.clear();
        activoMedicamento.setValue(false);
        gridMedicamentos.deselectAll();
    }

    private void eliminarMedicamento() {

        if (medicamentoSeleccionado == null) {
            Notification.show("Selecciona un medicamento.");
            return;
        }

        try {
            crudService.eliminar(
                    Medicamento.class,
                    medicamentoSeleccionado.getId()
            );

            Notification.show("Medicamento eliminado.");
            limpiarMedicamento();
            actualizarMedicamentos();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("No se pudo eliminar el medicamento.");
        }
    }

    private void actualizarMedicamentos() {
        gridMedicamentos.setItems(crudService.listarMedicamentosConLaboratorio());
    }

    // =====================================
    // INVENTARIOS
    // =====================================

    private Component crearVistaInventarios() {

        comboMedicamento.setItemLabelGenerator(Medicamento::getNombre);
        comboMedicamento.setWidthFull();

        gridInventarios.addColumn(Inventario::getId).setHeader("ID");
        gridInventarios.addColumn(Inventario::getLote).setHeader("Lote");
        gridInventarios.addColumn(Inventario::getStock).setHeader("Stock");

        gridInventarios.addColumn(
                i -> i.getMedicamento() != null
                        ? i.getMedicamento().getNombre()
                        : "Sin medicamento"
        ).setHeader("Medicamento");

        gridInventarios.addColumn(Inventario::getFechaVencimiento)
                .setHeader("Vencimiento");

        gridInventarios.addColumn(Inventario::getEstadoActivo)
                .setHeader("Activo");

        gridInventarios.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(
                loteInventario,
                stockInventario,
                vencimientoInventario,
                comboMedicamento,
                activoInventario
        );

        Button guardar = new Button("Guardar", e -> guardarInventario());
        Button limpiar = new Button("Limpiar", e -> limpiarInventario());
        Button eliminar = new Button("Eliminar", e -> eliminarInventario());

        gridInventarios.asSingleSelect().addValueChangeListener(event -> {
            inventarioSeleccionado = event.getValue();

            if (inventarioSeleccionado != null) {
                loteInventario.setValue(valor(inventarioSeleccionado.getLote()));

                stockInventario.setValue(
                        inventarioSeleccionado.getStock() != null
                                ? inventarioSeleccionado.getStock().toString()
                                : ""
                );

                vencimientoInventario.setValue(
                        inventarioSeleccionado.getFechaVencimiento()
                );

                comboMedicamento.setValue(inventarioSeleccionado.getMedicamento());

                activoInventario.setValue(
                        Boolean.TRUE.equals(inventarioSeleccionado.getEstadoActivo())
                );
            }
        });

        return new VerticalLayout(
                new H2("Control de inventarios"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridInventarios
        );
    }

    private void guardarInventario() {

        if (loteInventario.isEmpty()
                || stockInventario.isEmpty()
                || vencimientoInventario.isEmpty()
                || comboMedicamento.isEmpty()) {

            Notification.show("Completa todos los campos del inventario.");
            return;
        }

        try {
            Integer stock = Integer.valueOf(stockInventario.getValue());

            if (stock < 0) {
                Notification.show("El stock no puede ser negativo.");
                return;
            }

            boolean esNuevo = inventarioSeleccionado == null;

            Inventario inventario = esNuevo
                    ? new Inventario()
                    : inventarioSeleccionado;

            LocalDateTime ahora = LocalDateTime.now();

            if (esNuevo) {
                inventario.setFechaCreacion(ahora);
            }

            inventario.setFechaActualizacion(ahora);
            inventario.setLote(loteInventario.getValue());
            inventario.setStock(stock);
            inventario.setFechaVencimiento(vencimientoInventario.getValue());
            inventario.setMedicamento(comboMedicamento.getValue());
            inventario.setEstadoActivo(activoInventario.getValue());

            if (esNuevo) {
                crudService.guardar(inventario);
            } else {
                crudService.actualizar(inventario);
            }

            Notification.show("Inventario guardado correctamente.");
            limpiarInventario();
            actualizarInventarios();

        } catch (NumberFormatException ex) {
            Notification.show("El stock debe ser un número entero.");
        } catch (Exception ex) {
            Notification.show("Error al guardar inventario: " + ex.getMessage());
        }
    }

    private void limpiarInventario() {
        inventarioSeleccionado = null;
        loteInventario.clear();
        stockInventario.clear();
        vencimientoInventario.clear();
        comboMedicamento.clear();
        activoInventario.setValue(false);
        gridInventarios.deselectAll();
    }

    private void eliminarInventario() {

        if (inventarioSeleccionado == null) {
            Notification.show("Selecciona un inventario.");
            return;
        }

        try {
            crudService.eliminar(
                    Inventario.class,
                    inventarioSeleccionado.getId()
            );

            Notification.show("Inventario eliminado.");
            limpiarInventario();
            actualizarInventarios();

        } catch (Exception ex) {
            Notification.show("Error al eliminar inventario: " + ex.getMessage());
        }
    }

    private void actualizarInventarios() {
        gridInventarios.setItems(crudService.listarInventariosConMedicamento());
    }

    // =====================================
    // VENTAS
    // =====================================

    private Component crearVistaVentas() {

        gridVentas.addColumn(Venta::getId).setHeader("ID");
        gridVentas.addColumn(Venta::getEstado).setHeader("Estado");
        gridVentas.addColumn(Venta::getTotal).setHeader("Total");
        gridVentas.addColumn(Venta::getFechaCreacion).setHeader("Fecha");
        gridVentas.addColumn(Venta::getEstadoActivo).setHeader("Activo");
        gridVentas.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(estadoVenta, totalVenta, activoVenta);

        Button guardar = new Button("Guardar", e -> guardarVenta());
        Button limpiar = new Button("Limpiar", e -> limpiarVenta());
        Button eliminar = new Button("Eliminar", e -> eliminarVenta());

        gridVentas.asSingleSelect().addValueChangeListener(event -> {
            ventaSeleccionada = event.getValue();

            if (ventaSeleccionada != null) {
                estadoVenta.setValue(valor(ventaSeleccionada.getEstado()));

                totalVenta.setValue(
                        ventaSeleccionada.getTotal() != null
                                ? ventaSeleccionada.getTotal().toString()
                                : ""
                );

                activoVenta.setValue(
                        Boolean.TRUE.equals(ventaSeleccionada.getEstadoActivo())
                );
            }
        });

        return new VerticalLayout(
                new H2("Gestión de ventas"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridVentas
        );
    }

    private void guardarVenta() {

        if (estadoVenta.isEmpty() || totalVenta.isEmpty()) {
            Notification.show("Completa el estado y el total de la venta.");
            return;
        }

        try {
            Integer total = Integer.valueOf(totalVenta.getValue());

            if (total < 0) {
                Notification.show("El total no puede ser negativo.");
                return;
            }

            boolean esNueva = ventaSeleccionada == null;

            Venta venta = esNueva ? new Venta() : ventaSeleccionada;

            LocalDateTime ahora = LocalDateTime.now();

            if (esNueva) {
                venta.setFechaCreacion(ahora);
            }

            venta.setFechaActualizacion(ahora);
            venta.setEstado(estadoVenta.getValue());
            venta.setTotal(total);
            venta.setEstadoActivo(activoVenta.getValue());

            if (esNueva) {
                crudService.guardar(venta);
            } else {
                crudService.actualizar(venta);
            }

            Notification.show("Venta guardada correctamente.");
            limpiarVenta();
            actualizarVentas();
            actualizarCombos();

        } catch (NumberFormatException ex) {
            Notification.show("El total debe ser un número entero.");
        } catch (Exception ex) {
            Notification.show("Error al guardar la venta: " + ex.getMessage());
        }
    }

    private void limpiarVenta() {
        ventaSeleccionada = null;
        estadoVenta.clear();
        totalVenta.clear();
        activoVenta.setValue(false);
        gridVentas.deselectAll();
    }

    private void eliminarVenta() {

        if (ventaSeleccionada == null) {
            Notification.show("Selecciona una venta.");
            return;
        }

        try {
            crudService.eliminar(Venta.class, ventaSeleccionada.getId());

            Notification.show("Venta eliminada.");
            limpiarVenta();
            actualizarVentas();
            actualizarDetallesVenta();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("No se pudo eliminar la venta.");
        }
    }

    private void actualizarVentas() {
        gridVentas.setItems(crudService.listarVentasConDetalles());
    }

    // =====================================
    // DETALLES DE VENTA
    // =====================================

    private Component crearVistaDetallesVenta() {

        comboVenta.setItemLabelGenerator(v -> "Venta #" + v.getId());
        comboMedicamentoVenta.setItemLabelGenerator(Medicamento::getNombre);

        comboVenta.setWidthFull();
        comboMedicamentoVenta.setWidthFull();

        gridDetallesVenta.addColumn(DetalleVenta::getId).setHeader("ID");

        gridDetallesVenta.addColumn(
                d -> d.getVenta() != null ? d.getVenta().getId() : null
        ).setHeader("Venta");

        gridDetallesVenta.addColumn(
                d -> d.getMedicamento() != null
                        ? d.getMedicamento().getNombre()
                        : "Sin medicamento"
        ).setHeader("Medicamento");

        gridDetallesVenta.addColumn(DetalleVenta::getCantidad).setHeader("Cantidad");
        gridDetallesVenta.addColumn(DetalleVenta::getSubtotal).setHeader("Subtotal");
        gridDetallesVenta.addColumn(DetalleVenta::getEstadoActivo).setHeader("Activo");
        gridDetallesVenta.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(
                comboVenta,
                comboMedicamentoVenta,
                cantidadVenta,
                subtotalVenta,
                activoDetalleVenta
        );

        Button guardar = new Button("Guardar", e -> guardarDetalleVenta());
        Button limpiar = new Button("Limpiar", e -> limpiarDetalleVenta());
        Button eliminar = new Button("Eliminar", e -> eliminarDetalleVenta());

        gridDetallesVenta.asSingleSelect().addValueChangeListener(event -> {
            detalleVentaSeleccionado = event.getValue();

            if (detalleVentaSeleccionado != null) {
                comboVenta.setValue(detalleVentaSeleccionado.getVenta());

                comboMedicamentoVenta.setValue(
                        detalleVentaSeleccionado.getMedicamento()
                );

                cantidadVenta.setValue(
                        detalleVentaSeleccionado.getCantidad() != null
                                ? detalleVentaSeleccionado.getCantidad().toString()
                                : ""
                );

                subtotalVenta.setValue(
                        detalleVentaSeleccionado.getSubtotal() != null
                                ? detalleVentaSeleccionado.getSubtotal().toString()
                                : ""
                );

                activoDetalleVenta.setValue(
                        Boolean.TRUE.equals(detalleVentaSeleccionado.getEstadoActivo())
                );
            }
        });

        return new VerticalLayout(
                new H2("Detalles de venta"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridDetallesVenta
        );
    }

    private void guardarDetalleVenta() {

        if (comboVenta.isEmpty()
                || comboMedicamentoVenta.isEmpty()
                || cantidadVenta.isEmpty()
                || subtotalVenta.isEmpty()) {

            Notification.show("Completa todos los campos del detalle.");
            return;
        }

        try {
            Integer cantidad = Integer.valueOf(cantidadVenta.getValue());
            Integer subtotal = Integer.valueOf(subtotalVenta.getValue());

            if (cantidad <= 0 || subtotal < 0) {
                Notification.show("Verifica la cantidad y el subtotal.");
                return;
            }

            boolean esNuevo = detalleVentaSeleccionado == null;

            DetalleVenta detalle = esNuevo
                    ? new DetalleVenta()
                    : detalleVentaSeleccionado;

            LocalDateTime ahora = LocalDateTime.now();

            if (esNuevo) {
                detalle.setFechaCreacion(ahora);
            }

            detalle.setFechaActualizacion(ahora);
            detalle.setVenta(comboVenta.getValue());
            detalle.setMedicamento(comboMedicamentoVenta.getValue());
            detalle.setCantidad(cantidad);
            detalle.setSubtotal(subtotal);
            detalle.setEstadoActivo(activoDetalleVenta.getValue());

            if (esNuevo) {
                crudService.guardar(detalle);
            } else {
                crudService.actualizar(detalle);
            }

            Notification.show("Detalle de venta guardado.");
            limpiarDetalleVenta();
            actualizarDetallesVenta();
            actualizarVentas();

        } catch (NumberFormatException ex) {
            Notification.show("Cantidad y subtotal deben ser números enteros.");
        } catch (Exception ex) {
            Notification.show("Error al guardar detalle: " + ex.getMessage());
        }
    }

    private void limpiarDetalleVenta() {
        detalleVentaSeleccionado = null;
        comboVenta.clear();
        comboMedicamentoVenta.clear();
        cantidadVenta.clear();
        subtotalVenta.clear();
        activoDetalleVenta.setValue(false);
        gridDetallesVenta.deselectAll();
    }

    private void eliminarDetalleVenta() {

        if (detalleVentaSeleccionado == null) {
            Notification.show("Selecciona un detalle de venta.");
            return;
        }

        try {
            crudService.eliminar(
                    DetalleVenta.class,
                    detalleVentaSeleccionado.getId()
            );

            Notification.show("Detalle eliminado.");
            limpiarDetalleVenta();
            actualizarDetallesVenta();
            actualizarVentas();

        } catch (Exception ex) {
            Notification.show("Error al eliminar detalle: " + ex.getMessage());
        }
    }

    private void actualizarDetallesVenta() {
        gridDetallesVenta.setItems(crudService.listarDetallesVenta());
    }

    // =====================================
    // FORMULAS
    // =====================================

    private Component crearVistaFormulas() {

        gridFormulas.addColumn(Formula::getId).setHeader("ID");
        gridFormulas.addColumn(Formula::getNombre).setHeader("Nombre");
        gridFormulas.addColumn(Formula::getEstadoActivo).setHeader("Activo");
        gridFormulas.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(nombreFormula, activoFormula);

        Button guardar = new Button("Guardar", e -> guardarFormula());
        Button limpiar = new Button("Limpiar", e -> limpiarFormula());
        Button eliminar = new Button("Eliminar", e -> eliminarFormula());

        gridFormulas.asSingleSelect().addValueChangeListener(event -> {
            formulaSeleccionada = event.getValue();

            if (formulaSeleccionada != null) {
                nombreFormula.setValue(valor(formulaSeleccionada.getNombre()));

                activoFormula.setValue(
                        Boolean.TRUE.equals(formulaSeleccionada.getEstadoActivo())
                );
            }
        });

        return new VerticalLayout(
                new H2("Gestión de fórmulas"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridFormulas
        );
    }

    private void guardarFormula() {

        if (nombreFormula.isEmpty()) {
            Notification.show("Escribe el nombre de la fórmula.");
            return;
        }

        try {
            boolean esNueva = formulaSeleccionada == null;

            Formula formula = esNueva ? new Formula() : formulaSeleccionada;

            LocalDateTime ahora = LocalDateTime.now();

            if (esNueva) {
                formula.setFechaCreacion(ahora);
            }

            formula.setFechaActualizacion(ahora);
            formula.setNombre(nombreFormula.getValue());
            formula.setEstadoActivo(activoFormula.getValue());

            if (esNueva) {
                crudService.guardar(formula);
            } else {
                crudService.actualizar(formula);
            }

            Notification.show("Fórmula guardada correctamente.");
            limpiarFormula();
            actualizarFormulas();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("Error al guardar fórmula: " + ex.getMessage());
        }
    }

    private void limpiarFormula() {
        formulaSeleccionada = null;
        nombreFormula.clear();
        activoFormula.setValue(false);
        gridFormulas.deselectAll();
    }

    private void eliminarFormula() {

        if (formulaSeleccionada == null) {
            Notification.show("Selecciona una fórmula.");
            return;
        }

        try {
            crudService.eliminar(
                    Formula.class,
                    formulaSeleccionada.getId()
            );

            Notification.show("Fórmula eliminada.");
            limpiarFormula();
            actualizarFormulas();
            actualizarFormulaMedicamentos();
            actualizarCombos();

        } catch (Exception ex) {
            Notification.show("No se pudo eliminar la fórmula.");
        }
    }

    private void actualizarFormulas() {
        gridFormulas.setItems(crudService.listarFormulasConMedicamentos());
    }

    // =====================================
    // FORMULA - MEDICAMENTO
    // =====================================

    private Component crearVistaFormulaMedicamentos() {

        comboFormula.setItemLabelGenerator(Formula::getNombre);
        comboMedicamentoFormula.setItemLabelGenerator(Medicamento::getNombre);

        comboFormula.setWidthFull();
        comboMedicamentoFormula.setWidthFull();

        gridFormulaMedicamentos.addColumn(
                relacion -> relacion.getFormula() != null
                        ? relacion.getFormula().getNombre()
                        : "Sin fórmula"
        ).setHeader("Fórmula");

        gridFormulaMedicamentos.addColumn(
                relacion -> relacion.getMedicamento() != null
                        ? relacion.getMedicamento().getNombre()
                        : "Sin medicamento"
        ).setHeader("Medicamento");

        gridFormulaMedicamentos.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(comboFormula, comboMedicamentoFormula);

        Button guardar = new Button("Guardar relación", e -> guardarFormulaMedicamento());
        Button limpiar = new Button("Limpiar", e -> limpiarFormulaMedicamento());
        Button eliminar = new Button("Eliminar", e -> eliminarFormulaMedicamento());

        gridFormulaMedicamentos.asSingleSelect().addValueChangeListener(event -> {
            relacionSeleccionada = event.getValue();

            if (relacionSeleccionada != null) {
                comboFormula.setValue(relacionSeleccionada.getFormula());
                comboMedicamentoFormula.setValue(
                        relacionSeleccionada.getMedicamento()
                );
            }
        });

        return new VerticalLayout(
                new H2("Asociar medicamentos a fórmulas"),
                formulario,
                new HorizontalLayout(guardar, limpiar, eliminar),
                gridFormulaMedicamentos
        );
    }

    private void guardarFormulaMedicamento() {

        if (comboFormula.isEmpty() || comboMedicamentoFormula.isEmpty()) {
            Notification.show("Selecciona una fórmula y un medicamento.");
            return;
        }

        try {
            Formula formula = comboFormula.getValue();
            Medicamento medicamento = comboMedicamentoFormula.getValue();

            boolean esNueva = relacionSeleccionada == null;

            if (esNueva) {
                FormulaMedicamento relacion =
                        new FormulaMedicamento(formula, medicamento);

                crudService.guardar(relacion);

            } else {

                // Construir la clave compuesta de la nueva asociación.
                FormulaMedicamento.FormulaMedicamentoId nuevoId =
                        new FormulaMedicamento.FormulaMedicamentoId(
                                formula.getId(),
                                medicamento.getId()
                        );

                // Verificar si la nueva asociación ya existe.
                FormulaMedicamento existente =
                        crudService.buscarPorId(FormulaMedicamento.class, nuevoId);

                boolean mismaRelacion =
                        formula.getId().equals(relacionSeleccionada.getFormulaId())
                        && medicamento.getId().equals(
                                relacionSeleccionada.getMedicamentoId()
                        );

                if (existente != null && !mismaRelacion) {
                    Notification.show("Esa relación ya existe.");
                    return;
                }

                // Eliminar la asociación anterior.
                FormulaMedicamento.FormulaMedicamentoId idAnterior =
                        new FormulaMedicamento.FormulaMedicamentoId(
                                relacionSeleccionada.getFormulaId(),
                                relacionSeleccionada.getMedicamentoId()
                        );

                crudService.eliminar(FormulaMedicamento.class, idAnterior);

                // Crear la asociación con los nuevos valores.
                FormulaMedicamento nuevaRelacion =
                        new FormulaMedicamento(formula, medicamento);

                crudService.guardar(nuevaRelacion);
            }

            Notification.show(
                    esNueva
                            ? "Relación creada correctamente."
                            : "Relación actualizada correctamente."
            );

            limpiarFormulaMedicamento();
            actualizarFormulaMedicamentos();
            actualizarFormulas();

        } catch (Exception ex) {
            Notification.show("Error al guardar la relación: " + ex.getMessage());
        }
    }

    private void limpiarFormulaMedicamento() {
        relacionSeleccionada = null;
        comboFormula.clear();
        comboMedicamentoFormula.clear();
        gridFormulaMedicamentos.deselectAll();
    }

    private void eliminarFormulaMedicamento() {

        if (relacionSeleccionada == null) {
            Notification.show("Selecciona una relación para eliminar.");
            return;
        }

        try {
            FormulaMedicamento.FormulaMedicamentoId id =
                    new FormulaMedicamento.FormulaMedicamentoId(
                            relacionSeleccionada.getFormulaId(),
                            relacionSeleccionada.getMedicamentoId()
                    );

            crudService.eliminar(FormulaMedicamento.class, id);

            Notification.show("Relación eliminada.");
            limpiarFormulaMedicamento();
            actualizarFormulaMedicamentos();
            actualizarFormulas();

        } catch (Exception ex) {
            Notification.show("Error al eliminar la relación: " + ex.getMessage());
        }
    }

    private void actualizarFormulaMedicamentos() {
        gridFormulaMedicamentos.setItems(
                crudService.listarRelacionesFormulaMedicamento()
        );
    }

    // =====================================
    // ACTUALIZAR COMBOS
    // =====================================

    private void actualizarCombos() {

        comboLaboratorio.setItems(
                crudService.listarLaboratorios()
        );

        comboMedicamento.setItems(
                crudService.listarMedicamentosConLaboratorio()
        );

        comboVenta.setItems(
                crudService.listarVentasConDetalles()
        );

        comboMedicamentoVenta.setItems(
                crudService.listarMedicamentosConLaboratorio()
        );

        comboFormula.setItems(
                crudService.listarFormulasConMedicamentos()
        );

        comboMedicamentoFormula.setItems(
                crudService.listarMedicamentosConLaboratorio()
        );
    }

    // =====================================
    // METODO AUXILIAR
    // =====================================

    private String valor(String texto) {
        return texto != null ? texto : "";
    }
}
