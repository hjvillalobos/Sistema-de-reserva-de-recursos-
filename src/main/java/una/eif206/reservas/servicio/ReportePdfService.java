package una.eif206.reservas.servicio;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import una.eif206.reservas.modelo.Reserva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReportePdfService {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font FUENTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font FUENTE_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font FUENTE_ENCABEZADO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
    private static final Font FUENTE_CELDA = FontFactory.getFont(FontFactory.HELVETICA, 7);

    public void generarReporteProgramacion(MatrizProgramacion matriz, File archivo) throws IOException {
        Document documento = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            documento.add(new Paragraph("Programación de actividades", FUENTE_TITULO));
            documento.add(new Paragraph("Semana del " + matriz.getInicioSemana().format(FORMATO_FECHA)
                    + " al " + matriz.getFinSemana().format(FORMATO_FECHA), FUENTE_SUBTITULO));
            documento.add(new Paragraph(" "));

            List<LocalDate> dias = matriz.getDias();
            PdfPTable tabla = new PdfPTable(dias.size() + 1);
            tabla.setWidthPercentage(100);

            agregarEncabezado(tabla, "Hora");
            for (LocalDate dia : dias)
                agregarEncabezado(tabla, dia.getDayOfWeek() + "\n" + dia.format(FORMATO_FECHA));

            for (LocalTime hora : matriz.getHoras()) {
                agregarCelda(tabla, hora.toString(), true);
                for (LocalDate dia : dias) {
                    String texto = matriz.obtenerActividades(dia, hora).stream()
                            .map(this::describirActividad)
                            .collect(Collectors.joining("\n"));
                    agregarCelda(tabla, texto, false);
                }
            }
            documento.add(tabla);
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte.", e);
        } finally {
            if (documento.isOpen()) documento.close();
        }
    }

    public void generarReporteEstadisticasRecursos(List<EstadisticaCategoria> datos, LocalDate desde, LocalDate hasta,
                                                   byte[] imagenGrafico, File archivo) throws IOException {
        Document documento = new Document(PageSize.A4, 30, 30, 30, 30);
        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();
            documento.add(new Paragraph("Estadísticas de recursos reservados", FUENTE_TITULO));
            documento.add(new Paragraph("Período del " + desde.format(FORMATO_FECHA) + " al "
                    + hasta.format(FORMATO_FECHA), FUENTE_SUBTITULO));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            agregarEncabezado(tabla, "Categoría de recurso");
            agregarEncabezado(tabla, "Cantidad reservada");
            for (EstadisticaCategoria e : datos) {
                agregarCelda(tabla, e.getDescripcionCategoria(), false);
                agregarCelda(tabla, String.valueOf(e.getCantidad()), false);
            }
            documento.add(tabla);
            agregarGrafico(documento, imagenGrafico);
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte.", e);
        } finally {
            if (documento.isOpen()) documento.close();
        }
    }

    public void generarReporteEstadisticasActividades(List<EstadisticaSemana> datos, LocalDate desde, LocalDate hasta,
                                                      byte[] imagenGrafico, File archivo) throws IOException {
        Document documento = new Document(PageSize.A4, 30, 30, 30, 30);
        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();
            documento.add(new Paragraph("Estadísticas de actividades programadas", FUENTE_TITULO));
            documento.add(new Paragraph("Período del " + desde.format(FORMATO_FECHA) + " al "
                    + hasta.format(FORMATO_FECHA), FUENTE_SUBTITULO));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            agregarEncabezado(tabla, "Semana");
            agregarEncabezado(tabla, "Cantidad de actividades");
            for (EstadisticaSemana e : datos) {
                agregarCelda(tabla, e.getEtiqueta(), false);
                agregarCelda(tabla, String.valueOf(e.getCantidad()), false);
            }
            documento.add(tabla);
            agregarGrafico(documento, imagenGrafico);
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte.", e);
        } finally {
            if (documento.isOpen()) documento.close();
        }
    }

    private void agregarGrafico(Document documento, byte[] imagenGrafico) throws DocumentException, IOException {
        if (imagenGrafico == null || imagenGrafico.length == 0) return;
        documento.add(new Paragraph(" "));
        Image imagen = Image.getInstance(imagenGrafico);
        imagen.scaleToFit(documento.getPageSize().getWidth() - 60, 320);
        imagen.setAlignment(Element.ALIGN_CENTER);
        documento.add(imagen);
    }

    private String describirActividad(Reserva reserva) {
        String actividad = reserva.getActividad() != null ? reserva.getActividad() : "(sin nombre)";
        String funcionario = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : "?";
        String recurso = reserva.getRecurso() != null ? reserva.getRecurso().getDescripcion() : "?";
        return actividad + " - " + funcionario + " (" + recurso + ")";
    }

    private void agregarEncabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Paragraph(texto, FUENTE_ENCABEZADO));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBackgroundColor(new java.awt.Color(220, 220, 220));
        tabla.addCell(celda);
    }

    private void agregarCelda(PdfPTable tabla, String texto, boolean negrita) {
        PdfPCell celda = new PdfPCell(new Paragraph(texto == null ? "" : texto, negrita ? FUENTE_ENCABEZADO : FUENTE_CELDA));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }
}