package una.eif206.reservas.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.servicio.CategoriaService;
import una.eif206.reservas.servicio.ReservaService;
import una.eif206.reservas.servicio.ValidacionException;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservaController {

    @FXML private TextField txtFrase;
    @FXML private TextField txtActividad;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    @FXML private ListView<Categoria> listCategorias;
    @FXML private Label lblError;
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colId;
    @FXML private TableColumn<Reserva, String> colActividad;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, String> colHorario;
    @FXML private TableColumn<Reserva, String> colEstado;

    private final ReservaService reservaService = new ReservaService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<Reserva> datos = FXCollections.observableArrayList();

    private Usuario usuarioActual;

    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarReservas();
    }

    @FXML
    public void initialize() {
        listCategorias.setItems(FXCollections.observableArrayList(categoriaService.listarTodos()));
        listCategorias.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        colId.setCellValueFactory(new PropertyValueFactory<Reserva, String>("id"));
        colActividad.setCellValueFactory(new PropertyValueFactory<Reserva, String>("actividad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<Reserva, String>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Reserva, String>("estado"));
        colHorario.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(r.getHoraInicio() + " - " + r.getHoraFin());
        });

        tablaReservas.setItems(datos);
    }

    @FXML
    public void onReservar(ActionEvent event) {
        lblError.setText("");
        try {
            List<String> categoriasIds = new ArrayList<>();
            for (Categoria c : listCategorias.getSelectionModel().getSelectedItems()) {
                categoriasIds.add(c.getId());
            }

            String fecha = (dpFecha.getValue() == null) ? "" : dpFecha.getValue().toString();

            reservaService.intentarRegistrar(
                    txtActividad.getText(), fecha, txtHoraInicio.getText(), txtHoraFin.getText(),
                    usuarioActual.getId(), categoriasIds);

            limpiarFormulario();
            cargarReservas();
        } catch (ValidacionException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelarSeleccionada(ActionEvent event) {
        lblError.setText("");
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblError.setText("Seleccione una reserva de la lista para cancelar.");
            return;
        }
        try {
            reservaService.cancelar(seleccionada.getId(), usuarioActual.getId());
            cargarReservas();
        } catch (ValidacionException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    public void onLimpiar(ActionEvent event) {
        limpiarFormulario();
        lblError.setText("");
    }

    @FXML
    public void onExtraerIA(ActionEvent event) {
        lblError.setText("La función de llenado con IA todavía no está implementada.");
    }

    @FXML
    public void onImprimir(ActionEvent event) {
        lblError.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de reservas");
        fileChooser.setInitialFileName("reservas.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tablaReservas.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        String[] encabezados = {"Id", "Actividad", "Fecha", "Horario", "Estado"};
        List<String[]> filas = new ArrayList<>();
        for (Reserva r : datos) {
            filas.add(new String[]{
                    r.getId(), r.getActividad(), r.getFecha(),
                    r.getHoraInicio() + " - " + r.getHoraFin(), r.getEstado().toString()
            });
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Mis Reservas", encabezados, filas);
        } catch (IOException e) {
            lblError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void cargarReservas() {
        datos.clear();
        if (usuarioActual == null) return;
        List<Reserva> lista = reservaService.listarPorFuncionario(usuarioActual.getId());
        for (Reserva r : lista) {
            datos.add(r);
        }
    }

    private void limpiarFormulario() {
        txtFrase.clear();
        txtActividad.clear();
        dpFecha.setValue(null);
        txtHoraInicio.clear();
        txtHoraFin.clear();
        listCategorias.getSelectionModel().clearSelection();
    }
}