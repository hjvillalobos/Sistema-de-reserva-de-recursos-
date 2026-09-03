package una.eif206.reservas.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.DTO.CeldaCalendarizacionDTO;
import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.servicios.CategoriaService;
import una.eif206.reservas.servicios.CalendarizacionServicio;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalendarizacionController {

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<CategoriaDTO> comboCategoria;
    @FXML private GridPane gridMatriz;
    @FXML private Label lblError;

    private final CalendarizacionServicio calendarizacionServicio = new CalendarizacionServicio();
    private final CategoriaService categoriaService = new CategoriaService();

    private MatrizCalendarizacionDTO matrizActual;

    @FXML
    public void initialize() {
        comboCategoria.setItems(javafx.collections.FXCollections.observableArrayList(categoriaService.listarTodos()));
    }

    @FXML
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) gridMatriz.getScene().getWindow()).close();
    }

    @FXML
    public void onConsultar(ActionEvent event) {
        lblError.setText("");
        gridMatriz.getChildren().clear();

        if (dpFecha.getValue() == null || comboCategoria.getValue() == null) {
            lblError.setText("Debe seleccionar fecha y categoría.");
            return;
        }
        try {
            matrizActual = calendarizacionServicio.obtenerMatriz(dpFecha.getValue().toString(), comboCategoria.getValue().getId());
            dibujarMatriz(matrizActual);
        } catch (ValidacionException e) {
            lblError.setText(e.getMessage());
        }
    }

    private void dibujarMatriz(MatrizCalendarizacionDTO matriz) {
        List<String> horas = matriz.getHoras();
        List<RecursoDTO> recursos = matriz.getRecursos();
        gridMatriz.getChildren().clear();
        gridMatriz.getColumnConstraints().clear();
        gridMatriz.getRowConstraints().clear();
        gridMatriz.setHgap(1);
        gridMatriz.setVgap(1);
        gridMatriz.setStyle("-fx-background-color: #1E293B;"); // Borde del grid en azul oscuro

        ColumnConstraints colHora = new ColumnConstraints(80);
        gridMatriz.getColumnConstraints().add(colHora);
        for (int i = 0; i < recursos.size(); i++) {
            ColumnConstraints col = new ColumnConstraints(160);
            gridMatriz.getColumnConstraints().add(col);
        }

        RowConstraints filaEncabezado = new RowConstraints(45);
        gridMatriz.getRowConstraints().add(filaEncabezado);

        for (int i = 0; i < horas.size(); i++) {
            RowConstraints filaHora = new RowConstraints(38);
            gridMatriz.getRowConstraints().add(filaHora);
        }

        gridMatriz.add(construirCeldaEncabezado("HORA"), 0, 0);

        for (int col = 0; col < recursos.size(); col++) {
            gridMatriz.add(construirCeldaEncabezado(recursos.get(col).getDescripcion()), col + 1, 0);
        }

        for (int fila = 0; fila < horas.size(); fila++) {
            Label horaLabel = new Label(horas.get(fila));
            horaLabel.getStyleClass().add("grid-hora-cell");
            horaLabel.setMaxWidth(Double.MAX_VALUE);
            horaLabel.setMaxHeight(Double.MAX_VALUE);
            horaLabel.setAlignment(Pos.CENTER);
            gridMatriz.add(horaLabel, 0, fila + 1);

            for (int col = 0; col < recursos.size(); col++) {
                CeldaCalendarizacionDTO celda = matriz.obtenerCelda(fila, col);
                Label celdaLabel = construirLabelCelda(celda);
                gridMatriz.add(celdaLabel, col + 1, fila + 1);
            }
        }
    }

    private Label construirCeldaEncabezado(String texto) {
        Label label = new Label(texto.toUpperCase());
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("grid-header");
        return label;
    }

    private Label construirLabelCelda(CeldaCalendarizacionDTO celda) {
        Label label;
        if (celda.isOcupada()) {
            label = new Label(celda.getActividad() + "\n(" + celda.getNombreFuncionario() + ")");
            label.getStyleClass().add("grid-cell-ocupada");
        } else {
            label = new Label("Libre");
            label.getStyleClass().add("grid-cell-libre");
        }
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    @FXML
    public void onImprimir(ActionEvent event) {
        lblError.setText("");

        if (matrizActual == null) {
            lblError.setText("Se debe consultar una fecha y categoría.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de recursos");
        fileChooser.setInitialFileName("calendarizacion.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) gridMatriz.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        List<String> encabezadosLista = new ArrayList<>();
        encabezadosLista.add("Hora");
        for (RecursoDTO recurso : matrizActual.getRecursos()) {
            encabezadosLista.add(recurso.getDescripcion());
        }
        String[] encabezados = encabezadosLista.toArray(new String[0]);
        List<String[]> filas = new ArrayList<>();
        for (int f = 0; f < matrizActual.getHoras().size(); f++) {
            List<String> fila = new ArrayList<>();
            fila.add(matrizActual.getHoras().get(f));
            for (int c = 0; c < matrizActual.getRecursos().size(); c++) {
                CeldaCalendarizacionDTO celda = matrizActual.obtenerCelda(f, c);
                fila.add(celda.isOcupada() ? celda.getActividad() + "/" + celda.getNombreFuncionario() : "Libre");
            }
            filas.add(fila.toArray(new String[0]));
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Calendarización de Recursos", encabezados, filas);
        } catch (IOException e) {
            lblError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }
}