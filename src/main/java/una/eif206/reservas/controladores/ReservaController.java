package una.eif206.reservas.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.servicios.CategoriaService;
import una.eif206.reservas.servicios.ReservaServicio;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.util.ExtraccionIAException;
import una.eif206.reservas.util.ExtractorReservaIA;
import una.eif206.reservas.util.ReportePdfUtil;
import una.eif206.reservas.util.ReservaExtraida;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaController {

    @FXML private TextField txtReservaFrase;
    @FXML private TextField txtReservaActividad;
    @FXML private DatePicker dpReservaFecha;
    @FXML private TextField txtReservaHoraInicio;
    @FXML private TextField txtReservaHoraFin;
    @FXML private ListView<CategoriaDTO> lstReservaCategorias;
    @FXML private Label lblReservaError;
    @FXML private TableView<ReservaDTO> tblReservaListado;
    @FXML private TableColumn<ReservaDTO, String> colReservaId;
    @FXML private TableColumn<ReservaDTO, String> colReservaActividad;
    @FXML private TableColumn<ReservaDTO, String> colReservaFecha;
    @FXML private TableColumn<ReservaDTO, String> colReservaHorario;
    @FXML private TableColumn<ReservaDTO, String> colReservaEstado;

    private final ReservaServicio reservaService = new ReservaServicio();
    private final CategoriaService categoriaService = new CategoriaService();
    private final ExtractorReservaIA extractorIA = new ExtractorReservaIA();
    private final ObservableList<ReservaDTO> datos = FXCollections.observableArrayList();

    private UsuarioDTO usuarioActual;

    public void inicializar(UsuarioDTO usuario) {
        this.usuarioActual = usuario;
        cargarReservas();
    }

    @FXML
    public void initialize() {
        lstReservaCategorias.setItems(FXCollections.observableArrayList(categoriaService.listarTodos()));
        lstReservaCategorias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colReservaId.setCellValueFactory(new PropertyValueFactory<ReservaDTO, String>("id"));
        colReservaActividad.setCellValueFactory(new PropertyValueFactory<ReservaDTO, String>("actividad"));
        colReservaFecha.setCellValueFactory(new PropertyValueFactory<ReservaDTO, String>("fecha"));
        colReservaEstado.setCellValueFactory(new PropertyValueFactory<ReservaDTO, String>("estado"));
        colReservaHorario.setCellValueFactory(cellData -> {
            ReservaDTO r = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(r.getHoraInicio() + " - " + r.getHoraFin());
        });

        tblReservaListado.setItems(datos);
    }

    @FXML
    public void onReservar(ActionEvent event) {
        lblReservaError.setText("");

        if (dpReservaFecha.getValue() == null) {
            lblReservaError.setText("Debe seleccionar una fecha.");
            return;
        }

        try {
            List<String> categoriasIds = new ArrayList<>();
            for (CategoriaDTO c : lstReservaCategorias.getSelectionModel().getSelectedItems()) {
                categoriasIds.add(c.getId());
            }

            reservaService.intentarRegistrar(
                    txtReservaActividad.getText(), dpReservaFecha.getValue().toString(),
                    txtReservaHoraInicio.getText(), txtReservaHoraFin.getText(),
                    usuarioActual.getId(), categoriasIds);

            limpiarFormulario();
            cargarReservas();
        } catch (ValidacionException e) {
            lblReservaError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelarSeleccionada(ActionEvent event) {
        lblReservaError.setText("");
        ReservaDTO seleccionada = tblReservaListado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblReservaError.setText("Seleccione una reserva de la lista para cancelar.");
            return;
        }
        try {
            reservaService.cancelar(seleccionada.getId(), usuarioActual.getId());
            cargarReservas();
        } catch (ValidacionException e) {
            lblReservaError.setText(e.getMessage());
        }
    }

    @FXML
    public void onLimpiar(ActionEvent event) {
        limpiarFormulario();
        lblReservaError.setText("");
    }

    @FXML
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) tblReservaListado.getScene().getWindow()).close();
    }

    @FXML
    public void onExtraerIA(ActionEvent event) {
        lblReservaError.setText("");

        List<String> descripcionesDisponibles = new ArrayList<>();
        for (CategoriaDTO c : categoriaService.listarTodos()) {
            descripcionesDisponibles.add(c.getDescripcion());
        }

        try {
            ReservaExtraida extraido = extractorIA.extraer(txtReservaFrase.getText(), descripcionesDisponibles);

            txtReservaActividad.setText(extraido.getActividad());
            txtReservaHoraInicio.setText(extraido.getHoraInicio());
            txtReservaHoraFin.setText(extraido.getHoraFin());

            if (!extraido.getFecha().isBlank()) {
                try {
                    dpReservaFecha.setValue(LocalDate.parse(extraido.getFecha()));
                } catch (Exception e) {
                    // Si la IA devolvió una fecha en formato raro, el usuario la corrige a mano
                }
            }

            lstReservaCategorias.getSelectionModel().clearSelection();
            for (CategoriaDTO categoria : lstReservaCategorias.getItems()) {
                for (String descripcionExtraida : extraido.getCategoriasDescripcion()) {
                    if (categoria.getDescripcion().equalsIgnoreCase(descripcionExtraida.trim())) {
                        lstReservaCategorias.getSelectionModel().select(categoria);
                    }
                }
            }

        } catch (ExtraccionIAException e) {
            lblReservaError.setText(e.getMessage());
        }
    }

    @FXML
    public void onImprimir(ActionEvent event) {
        lblReservaError.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de reservas");
        fileChooser.setInitialFileName("reservas.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tblReservaListado.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        String[] encabezados = {"Id", "Actividad", "Fecha", "Horario", "Estado"};
        List<String[]> filas = new ArrayList<>();
        for (ReservaDTO r : datos) {
            filas.add(new String[]{
                    r.getId(), r.getActividad(), r.getFecha(),
                    r.getHoraInicio() + " - " + r.getHoraFin(), r.getEstado().toString()
            });
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Mis Reservas", encabezados, filas);
        } catch (IOException e) {
            lblReservaError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void cargarReservas() {
        datos.clear();
        if (usuarioActual == null) return;
        datos.addAll(reservaService.listarPorFuncionario(usuarioActual.getId()));
    }

    private void limpiarFormulario() {
        txtReservaFrase.clear();
        txtReservaActividad.clear();
        dpReservaFecha.setValue(null);
        txtReservaHoraInicio.clear();
        txtReservaHoraFin.clear();
        lstReservaCategorias.getSelectionModel().clearSelection();
    }
}