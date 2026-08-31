package una.eif206.reservas.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.servicio.MatrizProgramacion;
import una.eif206.reservas.servicio.ProgramacionService;
import una.eif206.reservas.servicio.ReportePdfService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramacionController {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblSemana;
    @FXML private ScrollPane scrollMatriz;

    private final ProgramacionService programacionService = new ProgramacionService();
    private final ReportePdfService reportePdfService = new ReportePdfService();

    private Usuario usuario;
    private LocalDate fechaReferencia;
    private MatrizProgramacion matrizActual;

    public void inicializar(Usuario usuario) {
        this.usuario = usuario;
        this.fechaReferencia = LocalDate.now();
        cargarSemana();
    }

    @FXML public void onSemanaAnterior() { fechaReferencia = fechaReferencia.minusWeeks(1); cargarSemana(); }
    @FXML public void onSemanaSiguiente() { fechaReferencia = fechaReferencia.plusWeeks(1); cargarSemana(); }
    @FXML public void onSemanaActual() { fechaReferencia = LocalDate.now(); cargarSemana(); }

    @FXML
    public void onGenerarPdf() {
        if (matrizActual == null) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte");
        chooser.setInitialFileName("programacion_" + matrizActual.getInicioSemana() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File archivo = chooser.showSaveDialog((Stage) lblSemana.getScene().getWindow());
        if (archivo == null) return;
        try {
            reportePdfService.generarReporteProgramacion(matrizActual, archivo);
            new Alert(Alert.AlertType.INFORMATION, "Reporte generado en: " + archivo.getAbsolutePath()).showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo generar el PDF: " + e.getMessage()).showAndWait();
        }
    }

    private void cargarSemana() {
        matrizActual = programacionService.obtenerMatrizSemana(usuario, fechaReferencia);
        lblSemana.setText("Semana del " + matrizActual.getInicioSemana().format(FORMATO_FECHA)
                + " al " + matrizActual.getFinSemana().format(FORMATO_FECHA));
        scrollMatriz.setContent(construirMatriz(matrizActual));
    }

    private GridPane construirMatriz(MatrizProgramacion matriz) {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.setPadding(new Insets(5));

        List<LocalDate> dias = matriz.getDias();
        grid.getColumnConstraints().add(new ColumnConstraints(70));
        for (int i = 0; i < dias.size(); i++) grid.getColumnConstraints().add(new ColumnConstraints(160));

        grid.add(celda("Hora", true), 0, 0);
        for (int c = 0; c < dias.size(); c++) {
            LocalDate dia = dias.get(c);
            grid.add(celda(dia.getDayOfWeek() + "\n" + dia.format(FORMATO_FECHA), true), c + 1, 0);
        }

        List<LocalTime> horas = matriz.getHoras();
        for (int f = 0; f < horas.size(); f++) {
            LocalTime hora = horas.get(f);
            grid.add(celda(hora.toString(), true), 0, f + 1);
            for (int c = 0; c < dias.size(); c++) {
                List<Reserva> actividades = matriz.obtenerActividades(dias.get(c), hora);
                String texto = actividades.stream()
                        .map(r -> r.getActividad() + "\n" + r.getFuncionario().getNombre())
                        .collect(Collectors.joining("\n---\n"));
                grid.add(celda(texto, false), c + 1, f + 1);
            }
        }
        return grid;
    }

    private Label celda(String texto, boolean encabezado) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setPadding(new Insets(3));
        if (encabezado) label.setStyle("-fx-font-weight: bold; -fx-background-color: #e0e0e0;");
        return label;
    }
}