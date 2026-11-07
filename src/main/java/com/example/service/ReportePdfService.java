
package com.example.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.example.model.Inventario;

@Service
public class ReportePdfService {

    private final CrudService crudService;

    public ReportePdfService(CrudService crudService) {
        this.crudService = crudService;
    }

    public byte[] generarReporteInventario() throws DocumentException {

        List<Inventario> inventarios =
                crudService.listarInventariosConMedicamento();

        try (ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate());

            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Título del reporte
            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 18);

            Paragraph titulo = new Paragraph(
                    "FARMA CARE - REPORTE DE INVENTARIO",
                    titleFont);

            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Fecha de generación
            Font normalFont = FontFactory.getFont(
                    FontFactory.HELVETICA, 10);

            String fechaActual = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            Paragraph fecha = new Paragraph(
                    "Fecha de generación: " + fechaActual,
                    normalFont);

            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);

            document.add(new Paragraph(" "));

            // Tabla del inventario
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            table.setWidths(new float[]{
                0.7f, 2.2f, 2.0f, 1.5f, 1.0f, 1.8f, 1.0f
            });

            String[] encabezados = {
                "ID",
                "Medicamento",
                "Laboratorio",
                "Lote",
                "Stock",
                "Vencimiento",
                "Estado"
            };

            Font headerFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 9);

            for (String encabezado : encabezados) {
                Paragraph texto = new Paragraph(
                        encabezado, headerFont);

                table.addCell(texto);
            }

            // Repetir encabezado si la tabla ocupa varias páginas
            table.setHeaderRows(1);

            Font bodyFont = FontFactory.getFont(
                    FontFactory.HELVETICA, 8);

            DateTimeFormatter formatoFecha =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Inventario inventario : inventarios) {

                agregarCelda(table,
                        String.valueOf(inventario.getId()), bodyFont);

                String nombreMedicamento = "";
                String nombreLaboratorio = "";

                if (inventario.getMedicamento() != null) {

                    nombreMedicamento =
                            inventario.getMedicamento().getNombre() != null
                                    ? inventario.getMedicamento().getNombre()
                                    : "";

                    if (inventario.getMedicamento().getLaboratorio() != null) {

                        nombreLaboratorio =
                                inventario.getMedicamento()
                                        .getLaboratorio().getNombre() != null
                                ? inventario.getMedicamento()
                                        .getLaboratorio().getNombre()
                                : "";
                    }
                }

                agregarCelda(table, nombreMedicamento, bodyFont);
                agregarCelda(table, nombreLaboratorio, bodyFont);

                agregarCelda(table,
                        inventario.getLote() != null
                                ? inventario.getLote() : "",
                        bodyFont);

                agregarCelda(table,
                        String.valueOf(inventario.getStock()),
                        bodyFont);

                String vencimiento =
                        inventario.getFechaVencimiento() != null
                                ? inventario.getFechaVencimiento()
                                        .format(formatoFecha)
                                : "Sin fecha";

                agregarCelda(table, vencimiento, bodyFont);

                String estado =
                        Boolean.TRUE.equals(inventario.getEstadoActivo())
                                ? "Activo" : "Inactivo";

                agregarCelda(table, estado, bodyFont);
            }

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Total de registros: " + inventarios.size(),
                    normalFont));

            document.close();

            return outputStream.toByteArray();

        } catch (java.io.IOException e) {
            throw new DocumentException(
                    "Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    private void agregarCelda(
            PdfPTable table, String valor, Font font) {

        table.addCell(new Paragraph(
                valor != null ? valor : "", font));
    }
}
