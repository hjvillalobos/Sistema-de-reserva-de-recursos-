package una.eif206.reservas.controladores;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.EstadisticaCategoriaDTO;
import una.eif206.reservas.DTO.EstadisticaSemanaDTO;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.servicios.EstadisticasServicio;
import una.eif206.reservas.util.ReportePdfUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasController {

    @FXML private DatePicker dpDesdeRecursos;
    @FXML private DatePicker dpHastaRecursos;
    @FXML private TableView<EstadisticaCategoriaDTO> tablaRecursos;
    @FXML private TableColumn<EstadisticaCategoriaDTO, String> colCategoria;
    @FXML private TableColumn<EstadisticaCategoriaDTO, Long> colCantidadRecursos;
    @FXML private BarChart<String, Number> chartRecursos;
    @FXML private Label lblErrorRecursos;

    @FXML private DatePicker dpDesdeActividades;
    @FXML private DatePicker dpHastaActividades;
    @FXML private TableView<EstadisticaSemanaDTO> tablaActividades;
    @FXML private TableColumn<EstadisticaSemanaDTO, String> colSemana;
    @FXML private TableColumn<EstadisticaSemanaDTO, Long> colCantidadActividades;
    @FXML private BarChart<String, Number> chartActividades;
    @FXML private Label lblErrorActividades;

    private final EstadisticasServicio estadisticasServicio = new EstadisticasServicio();

    private List<EstadisticaCategoriaDTO> datosRecursosActuales;
    private List<EstadisticaSemanaDTO> datosActividadesActuales;

    @FXML
    public void initialize() {
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("descripcionCategoria"));
        colCantidadRecursos.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSemana.setCellValueFactory(new PropertyValueFactory<>("etiqueta"));
        colCantidadActividades.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        LocalDate hoy = LocalDate.now();
        dpHastaRecursos.setValue(hoy);
        dpDesdeRecursos.setValue(hoy.minusMonths(1));
        dpHastaActividades.setValue(hoy);
        dpDesdeActividades.setValue(hoy.minusMonths(1));
    }
    @FXML
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) tablaRecursos.getScene().getWindow()).close();
    }
    @FXML
    public void onConsultarRecursos(ActionEvent event) {
        lblErrorRecursos.setText("");
        try {
            String desde = dpDesdeRecursos.getValue().toString();
            String hasta = dpHastaRecursos.getValue().toString();
            datosRecursosActuales = estadisticasServicio.estadisticasRecursos(desde, hasta);

            tablaRecursos.getItems().setAll(datosRecursosActuales);

            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Recursos reservados");
            for (EstadisticaCategoriaDTO dato : datosRecursosActuales) {
                serie.getData().add(new XYChart.Data<>(dato.getDescripcionCategoria(), dato.getCantidad()));
            }
            chartRecursos.getData().setAll(serie);
        } catch (ValidacionException e) {
            lblErrorRecursos.setText(e.getMessage());
        }
    }

    @FXML
    public void onConsultarActividades(ActionEvent event) {
        lblErrorActividades.setText("");
        try {
            String desde = dpDesdeActividades.getValue().toString();
            String hasta = dpHastaActividades.getValue().toString();
            datosActividadesActuales = estadisticasServicio.estadisticasActividades(desde, hasta);

            tablaActividades.getItems().setAll(datosActividadesActuales);

            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Actividades programadas");
            for (EstadisticaSemanaDTO dato : datosActividadesActuales) {
                serie.getData().add(new XYChart.Data<>(dato.getEtiqueta(), dato.getCantidad()));
            }
            chartActividades.getData().setAll(serie);
        } catch (ValidacionException e) {
            lblErrorActividades.setText(e.getMessage());
        }
    }

    @FXML
    public void onGenerarPdfRecursos(ActionEvent event) {
        lblErrorRecursos.setText("");
        if (datosRecursosActuales == null) {
            lblErrorRecursos.setText("Primero debe consultar las estadísticas de recursos.");
            return;
        }
        File archivo = elegirArchivo("estadisticas_recursos.pdf");
        if (archivo == null) {
            return;
        }
        try {
            String[] encabezados = {"Categoría de recurso", "Cantidad"};
            List<String[]> filas = new ArrayList<>();
            for (EstadisticaCategoriaDTO dato : datosRecursosActuales) {
                filas.add(new String[]{dato.getDescripcionCategoria(), String.valueOf(dato.getCantidad())});
            }
            byte[] imagen = capturarGrafico(chartRecursos);
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Estadísticas de Recursos Reservados",
                    encabezados, filas, imagen);
        } catch (IOException e) {
            lblErrorRecursos.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    @FXML
    public void onGenerarPdfActividades(ActionEvent event) {
        lblErrorActividades.setText("");
        if (datosActividadesActuales == null) {
            lblErrorActividades.setText("Primero debe consultar las estadísticas de actividades.");
            return;
        }
        File archivo = elegirArchivo("estadisticas_actividades.pdf");
        if (archivo == null) {
            return;
        }
        try {
            String[] encabezados = {"Semana", "Cantidad de actividades"};
            List<String[]> filas = new ArrayList<>();
            for (EstadisticaSemanaDTO dato : datosActividadesActuales) {
                filas.add(new String[]{dato.getEtiqueta(), String.valueOf(dato.getCantidad())});
            }
            byte[] imagen = capturarGrafico(chartActividades);
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Estadísticas de Actividades Programadas",
                    encabezados, filas, imagen);
        } catch (IOException e) {
            lblErrorActividades.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private byte[] capturarGrafico(BarChart<String, Number> chart) throws IOException {
        WritableImage snapshot = chart.snapshot(new SnapshotParameters(), null);
        BufferedImage imagenAwt = SwingFXUtils.fromFXImage(snapshot, null);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(imagenAwt, "png", salida);
        return salida.toByteArray();
    }

    private File elegirArchivo(String nombreSugerido) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte de estadísticas");
        chooser.setInitialFileName(nombreSugerido);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tablaRecursos.getScene().getWindow();
        return chooser.showSaveDialog(stage);
    }
}