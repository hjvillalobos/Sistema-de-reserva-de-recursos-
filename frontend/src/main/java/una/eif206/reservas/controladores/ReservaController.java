package una.eif206.reservas.controladores;

import una.eif206.reservas.cliente.ServiciosCliente;
import una.eif206.reservas.cliente.TareasUI;

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
import una.eif206.reservas.DTO.UsuarioSesionDTO;
import una.eif206.reservas.api.CategoriaApi;
import una.eif206.reservas.api.ReservaApi;
import una.eif206.reservas.excepciones.ValidacionException;
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

    private final ReservaApi reservaService = ServiciosCliente.obtener().reservas();
    private final CategoriaApi categoriaService = ServiciosCliente.obtener().categorias();
    private final ExtractorReservaIA extractorIA = new ExtractorReservaIA();
    private final ObservableList<ReservaDTO> datos = FXCollections.observableArrayList();

    private UsuarioSesionDTO usuarioActual;

    public void inicializar(UsuarioSesionDTO usuario) {
        this.usuarioActual = usuario;
        TareasUI.ejecutar(lblReservaError, categoriaService::listarTodos, categorias -> {
            lstReservaCategorias.setItems(FXCollections.observableArrayList(categorias));
            cargarReservas();
        });
    }

    @FXML
    public void initialize() {
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
        if (dpReservaFecha.getValue() == null) {
            lblReservaError.setText("Debe indicar la fecha.");
            return;
        }
        String actividad = txtReservaActividad.getText();
        String fecha = dpReservaFecha.getValue().toString();
        String inicio = txtReservaHoraInicio.getText();
        String fin = txtReservaHoraFin.getText();
        String id = usuarioActual.getId();
        List<String> categorias = lstReservaCategorias.getSelectionModel().getSelectedItems().stream().map(CategoriaDTO::getId).toList();
        TareasUI.ejecutar(lblReservaError,
                () -> reservaService.intentarRegistrar(actividad, fecha, inicio, fin, id, categorias),
                resultado -> { limpiarFormulario(); cargarReservas(); });
    }

    @FXML
    public void onCancelarSeleccionada(ActionEvent event) {
        ReservaDTO seleccionada = tblReservaListado.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblReservaError.setText("Seleccione una reserva de la lista para cancelar.");
            return;
        }
        String id = usuarioActual.getId();
        TareasUI.ejecutar(lblReservaError, () -> { reservaService.cancelar(seleccionada.getId(), id); return null; },
                resultado -> cargarReservas());
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
        String frase = txtReservaFrase.getText();
        List<String> categorias = lstReservaCategorias.getItems().stream().map(CategoriaDTO::getDescripcion).toList();
        TareasUI.ejecutar(lblReservaError, () -> extractorIA.extraer(frase, categorias), extraido -> {
            txtReservaActividad.setText(extraido.getActividad());
            txtReservaHoraInicio.setText(extraido.getHoraInicio());
            txtReservaHoraFin.setText(extraido.getHoraFin());
            if (!extraido.getFecha().isBlank()) {
                try { dpReservaFecha.setValue(LocalDate.parse(extraido.getFecha())); }
                catch (java.time.format.DateTimeParseException e) { lblReservaError.setText("Revise la fecha extraída."); }
            }
            lstReservaCategorias.getSelectionModel().clearSelection();
            for (CategoriaDTO categoria : lstReservaCategorias.getItems())
                for (String descripcion : extraido.getCategoriasDescripcion())
                    if (categoria.getDescripcion().equalsIgnoreCase(descripcion.trim()))
                        lstReservaCategorias.getSelectionModel().select(categoria);
        });
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
        if (usuarioActual == null) return;
        String id = usuarioActual.getId();
        TareasUI.ejecutar(lblReservaError, () -> reservaService.listarPorFuncionario(id), reservas -> datos.setAll(reservas));
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