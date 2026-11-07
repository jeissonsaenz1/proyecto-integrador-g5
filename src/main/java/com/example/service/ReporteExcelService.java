
package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.model.Inventario;

@Service
public class ReporteExcelService {

    private final CrudService crudService;

    public ReporteExcelService(CrudService crudService) {
        this.crudService = crudService;
    }

    public byte[] generarReporteInventario() throws IOException {

        List<Inventario> inventarios =
                crudService.listarInventariosConMedicamento();

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventario");

            // Estilo del encabezado
            CellStyle headerStyle = workbook.createCellStyle();

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Estilo para las fechas
            CreationHelper creationHelper = workbook.getCreationHelper();

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(
                    creationHelper.createDataFormat()
                            .getFormat("dd/mm/yyyy"));

            // Encabezados
            String[] encabezados = {
                "ID",
                "Medicamento",
                "Laboratorio",
                "Lote",
                "Stock",
                "Fecha de vencimiento",
                "Estado"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
                cell.setCellStyle(headerStyle);
            }

            // Información del inventario
            int fila = 1;

            for (Inventario inventario : inventarios) {

                Row row = sheet.createRow(fila++);

                row.createCell(0).setCellValue(
                        inventario.getId() != null
                                ? inventario.getId() : 0);

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

                row.createCell(1).setCellValue(nombreMedicamento);
                row.createCell(2).setCellValue(nombreLaboratorio);

                row.createCell(3).setCellValue(
                        inventario.getLote() != null
                                ? inventario.getLote() : "");

                row.createCell(4).setCellValue(
                        inventario.getStock() != null
                                ? inventario.getStock() : 0);

                Cell fechaCell = row.createCell(5);

                LocalDateTime fechaVencimiento =
                        inventario.getFechaVencimiento();

                if (fechaVencimiento != null) {
                    Date fecha = Date.from(
                            fechaVencimiento
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant());

                    fechaCell.setCellValue(fecha);
                    fechaCell.setCellStyle(dateStyle);
                } else {
                    fechaCell.setCellValue("");
                }

                row.createCell(6).setCellValue(
                        Boolean.TRUE.equals(inventario.getEstadoActivo())
                                ? "Activo" : "Inactivo");
            }

            // Ajustar automáticamente el ancho de las columnas
            for (int i = 0; i < encabezados.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Mantener visible el encabezado al desplazarse
            sheet.createFreezePane(0, 1);

            // Filtro en los encabezados
            if (!inventarios.isEmpty()) {
                sheet.setAutoFilter(
                        new org.apache.poi.ss.util.CellRangeAddress(
                                0, inventarios.size(),
                                0, encabezados.length - 1));
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }
}
