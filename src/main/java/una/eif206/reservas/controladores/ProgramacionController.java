package una.eif206.reservas.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.CeldaProgramacionDTO;
import una.eif206.reservas.DTO.MatrizProgramacionDTO;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.servicios.ProgramacionServicio;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgramacionController {

    @FXML private Label lblSemana;
    @FXML private Label lblError;
    @FXML private GridPane gridMatriz;

    private final ProgramacionServicio programacionServicio = new ProgramacionServicio();

    private String fechaReferencia;
    private MatrizProgramacionDTO matrizActual;

    @FXML
    public void initialize() {
        fechaReferencia = LocalDate.now().toString();
        cargarSemana();
    }

    @FXML
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) gridMatriz.getScene().getWindow()).close();
    }

    @FXML
    public void onSemanaAnterior(ActionEvent event) {
        fechaReferencia = LocalDate.parse(fechaReferencia).minusWeeks(1).toString();
        cargarSemana();
    }

    @FXML
    public void onSemanaSiguiente(ActionEvent event) {
        fechaReferencia = LocalDate.parse(fechaReferencia).plusWeeks(1).toString();
        cargarSemana();
    }

    @FXML
    public void onSemanaActual(ActionEvent event) {
        fechaReferencia = LocalDate.now().toString();
        cargarSemana();
    }

    private void cargarSemana() {
        lblError.setText("");
        try {
            matrizActual = programacionServicio.obtenerMatrizSemana(fechaReferencia);
            lblSemana.setText("Semana del " + matrizActual.getInicioSemana() + " al " + matrizActual.getFinSemana());
            dibujarMatriz(matrizActual);
        } catch (ValidacionException e) {
            lblError.setText(e.getMessage());
        }
    }

    private void dibujarMatriz(MatrizProgramacionDTO matriz) {
        List<String> horas = matriz.getHoras();
        List<String> dias = matriz.getDias();

        gridMatriz.getChildren().clear();
        gridMatriz.getColumnConstraints().clear();
        gridMatriz.getRowConstraints().clear();
        gridMatriz.setHgap(1);
        gridMatriz.setVgap(1);
        gridMatriz.setStyle("-fx-background-color: #1E293B;"); // Borde del grid en azul oscuro

        ColumnConstraints colHora = new ColumnConstraints(80);
        gridMatriz.getColumnConstraints().add(colHora);
        for (int i = 0; i < dias.size(); i++) {
            gridMatriz.getColumnConstraints().add(new ColumnConstraints(150));
        }

        gridMatriz.getRowConstraints().add(new RowConstraints(45));
        for (int i = 0; i < horas.size(); i++) {
            gridMatriz.getRowConstraints().add(new RowConstraints(42));
        }

        gridMatriz.add(construirCeldaEncabezado("HORA"), 0, 0);
        for (int col = 0; col < dias.size(); col++) {
            gridMatriz.add(construirCeldaEncabezado(dias.get(col)), col + 1, 0);
        }

        for (int fila = 0; fila < horas.size(); fila++) {
            Label horaLabel = new Label(horas.get(fila));
            horaLabel.getStyleClass().add("grid-hora-cell");
            horaLabel.setMaxWidth(Double.MAX_VALUE);
            horaLabel.setMaxHeight(Double.MAX_VALUE);
            horaLabel.setAlignment(Pos.CENTER);
            gridMatriz.add(horaLabel, 0, fila + 1);

            for (int col = 0; col < dias.size(); col++) {
                CeldaProgramacionDTO celda = matriz.obtenerCelda(fila, col);
                gridMatriz.add(construirLabelCelda(celda), col + 1, fila + 1);
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

    private Label construirLabelCelda(CeldaProgramacionDTO celda) {
        Label label;
        if (celda.isOcupada()) {
            label = new Label(String.join("\n", celda.getActividades()));
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
            lblError.setText("Debe consultar una semana primero.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de programación");
        fileChooser.setInitialFileName("programacion_" + matrizActual.getInicioSemana() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) gridMatriz.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);
        if (archivo == null) {
            return;
        }

        List<String> encabezadosLista = new ArrayList<>();
        encabezadosLista.add("Hora");
        encabezadosLista.addAll(matrizActual.getDias());
        String[] encabezados = encabezadosLista.toArray(new String[0]);

        List<String[]> filas = new ArrayList<>();
        for (int f = 0; f < matrizActual.getHoras().size(); f++) {
            List<String> fila = new ArrayList<>();
            fila.add(matrizActual.getHoras().get(f));
            for (int c = 0; c < matrizActual.getDias().size(); c++) {
                CeldaProgramacionDTO celda = matrizActual.obtenerCelda(f, c);
                fila.add(celda.isOcupada() ? String.join(" | ", celda.getActividades()) : "Libre");
            }
            filas.add(fila.toArray(new String[0]));
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Programación de Actividades", encabezados, filas);
        } catch (IOException e) {
            lblError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }
}