// util/ReportePdfUtil.java
package una.eif206.reservas.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportePdfUtil {

    private ReportePdfUtil() {}

    public static void generarReporteTabla(String rutaSalida, String titulo,
                                           String[] encabezados, List<String[]> filas) throws IOException {
        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaSalida));
            documento.open();

            Font fuenteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph parrafoTitulo = new Paragraph(titulo, fuenteTitulo);
            parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
            parrafoTitulo.setSpacingAfter(20);
            documento.add(parrafoTitulo);

            PdfPTable tabla = new PdfPTable(encabezados.length);
            tabla.setWidthPercentage(100);

            Font fuenteEncabezado = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(new Color(200, 0, 0));
                tabla.addCell(celda);
            }

            for (String[] fila : filas) {
                for (String valor : fila) {
                    tabla.addCell(valor);
                }
            }

            documento.add(tabla);
        } catch (DocumentException e) {
            throw new IOException("Error al generar el PDF", e);
        } finally {
            documento.close();
        }
    }
    public static void generarReporteTabla(String rutaSalida, String titulo, String[] encabezados,
                                           List<String[]> filas, byte[] imagenGrafico) throws IOException {
        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaSalida));
            documento.open();

            Font fuenteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph parrafoTitulo = new Paragraph(titulo, fuenteTitulo);
            parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
            parrafoTitulo.setSpacingAfter(20);
            documento.add(parrafoTitulo);

            PdfPTable tabla = new PdfPTable(encabezados.length);
            tabla.setWidthPercentage(100);

            Font fuenteEncabezado = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(new Color(200, 0, 0));
                tabla.addCell(celda);
            }

            for (String[] fila : filas) {
                for (String valor : fila) {
                    tabla.addCell(valor);
                }
            }

            documento.add(tabla);

            if (imagenGrafico != null && imagenGrafico.length > 0) {
                documento.add(new Paragraph(" "));
                com.lowagie.text.Image imagen = com.lowagie.text.Image.getInstance(imagenGrafico);
                imagen.scaleToFit(500, 300);
                imagen.setAlignment(Element.ALIGN_CENTER);
                documento.add(imagen);
            }
        } catch (DocumentException e) {
            throw new IOException("Error al generar el PDF", e);
        } finally {
            documento.close();
        }
    }
}