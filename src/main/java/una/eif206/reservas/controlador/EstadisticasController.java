package una.eif206.reservas.controlador;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.servicio.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class EstadisticasController {
    @FXML private DatePicker dpDesdeRecursos, dpHastaRecursos;
    @FXML private TableView<EstadisticaCategoria> tablaRecursos;
    @FXML private TableColumn<EstadisticaCategoria, String> colCategoria;
    @FXML private TableColumn<EstadisticaCategoria, Long> colCantidadRecursos;
    @FXML private BarChart<String, Number> chartRecursos;

    @FXML private DatePicker dpDesdeActividades, dpHastaActividades;
    @FXML private TableView<EstadisticaSemana> tablaActividades;
    @FXML private TableColumn<EstadisticaSemana, String> colSemana;
    @FXML private TableColumn<EstadisticaSemana, Long> colCantidadActividades;
    @FXML private BarChart<String, Number> chartActividades;

    private final EstadisticasService estadisticasService = new EstadisticasService();
    private final ReportePdfService reportePdfService = new ReportePdfService();

    private Usuario usuario;
    private List<EstadisticaCategoria> datosRecursosActuales;
    private List<EstadisticaSemana> datosActividadesActuales;

    @FXML
    public void initialize() {
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("descripcionCategoria"));
        colCantidadRecursos.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSemana.setCellValueFactory(new PropertyValueFactory<>("etiqueta"));
        colCantidadActividades.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
    }

    public void inicializar(Usuario usuario) {
        this.usuario = usuario;
        LocalDate hoy = LocalDate.now();
        dpHastaRecursos.setValue(hoy); dpDesdeRecursos.setValue(hoy.minusMonths(1));
        dpHastaActividades.setValue(hoy); dpDesdeActividades.setValue(hoy.minusMonths(1));
    }

    @FXML
    public void onConsultarRecursos() {
        datosRecursosActuales = estadisticasService.estadisticasRecursos(
                usuario, dpDesdeRecursos.getValue(), dpHastaRecursos.getValue());
        tablaRecursos.getItems().setAll(datosRecursosActuales);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (EstadisticaCategoria d : datosRecursosActuales)
            serie.getData().add(new XYChart.Data<>(d.getDescripcionCategoria(), d.getCantidad()));
        chartRecursos.getData().setAll(serie);
    }

    @FXML
    public void onConsultarActividades() {
        datosActividadesActuales = estadisticasService.estadisticasActividades(
                usuario, dpDesdeActividades.getValue(), dpHastaActividades.getValue());
        tablaActividades.getItems().setAll(datosActividadesActuales);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (EstadisticaSemana d : datosActividadesActuales)
            serie.getData().add(new XYChart.Data<>(d.getEtiqueta(), d.getCantidad()));
        chartActividades.getData().setAll(serie);
    }

    @FXML
    public void onGenerarPdfRecursos() throws IOException {
        File archivo = elegirArchivo("estadisticas_recursos.pdf");
        if (archivo == null) return;
        byte[] imagen = capturarGrafico(chartRecursos);
        reportePdfService.generarReporteEstadisticasRecursos(datosRecursosActuales,
                dpDesdeRecursos.getValue(), dpHastaRecursos.getValue(), imagen, archivo);
    }

    @FXML
    public void onGenerarPdfActividades() throws IOException {
        File archivo = elegirArchivo("estadisticas_actividades.pdf");
        if (archivo == null) return;
        byte[] imagen = capturarGrafico(chartActividades);
        reportePdfService.generarReporteEstadisticasActividades(datosActividadesActuales,
                dpDesdeActividades.getValue(), dpHastaActividades.getValue(), imagen, archivo);
    }

    private byte[] capturarGrafico(BarChart<String, Number> chart) throws IOException {
        WritableImage snapshot = chart.snapshot(new SnapshotParameters(), null);
        BufferedImage imagenAwt = SwingFXUtils.fromFXImage(snapshot, null);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(imagenAwt, "png", salida);
        return salida.toByteArray();
    }

    private File elegirArchivo(String nombre) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(nombre);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        return chooser.showSaveDialog((Stage) tablaRecursos.getScene().getWindow());
    }
}