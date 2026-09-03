package una.eif206.reservas.controladores;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.servicios.FuncionarioServicio;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioController {

    @FXML private TextField txtFuncionarioBusquedaId;
    @FXML private TextField txtFuncionarioBusquedaNombre;
    @FXML private TextField txtFuncionarioId;
    @FXML private TextField txtFuncionarioNombre;
    @FXML private TextField txtFuncionarioTelefono;
    @FXML private TableView<FuncionarioDTO> tblFuncionarioListado;
    @FXML private TableColumn<FuncionarioDTO, String> colFuncionarioId;
    @FXML private TableColumn<FuncionarioDTO, String> colFuncionarioNombre;
    @FXML private TableColumn<FuncionarioDTO, String> colFuncionarioTelefono;
    @FXML private Label lblFuncionarioError;

    private final FuncionarioServicio funcionarioService = new FuncionarioServicio();
    private final ObservableList<FuncionarioDTO> datos = FXCollections.observableArrayList();
    private boolean editandoExistente = false;

    @FXML
    public void initialize() {
        colFuncionarioId.setCellValueFactory(new PropertyValueFactory<FuncionarioDTO, String>("id"));
        colFuncionarioNombre.setCellValueFactory(new PropertyValueFactory<FuncionarioDTO, String>("nombre"));
        colFuncionarioTelefono.setCellValueFactory(new PropertyValueFactory<FuncionarioDTO, String>("telefono"));
        tblFuncionarioListado.setItems(datos);

        tblFuncionarioListado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FuncionarioDTO>() {
            @Override
            public void changed(ObservableValue<? extends FuncionarioDTO> observable,
                                FuncionarioDTO valorAnterior, FuncionarioDTO valorNuevo) {
                if (valorNuevo != null) {
                    txtFuncionarioId.setText(valorNuevo.getId());
                    txtFuncionarioId.setEditable(false);
                    txtFuncionarioNombre.setText(valorNuevo.getNombre());
                    txtFuncionarioTelefono.setText(valorNuevo.getTelefono());
                    editandoExistente = true;
                }
            }
        });

        cargarTodos();
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        datos.clear();
        String idB = txtFuncionarioBusquedaId.getText() == null ? "" : txtFuncionarioBusquedaId.getText().toLowerCase();
        String nombreB = txtFuncionarioBusquedaNombre.getText() == null ? "" : txtFuncionarioBusquedaNombre.getText().toLowerCase();

        for (FuncionarioDTO f : funcionarioService.obtenerTodos()) {
            boolean coincideId = idB.isEmpty() || f.getId().toLowerCase().contains(idB);
            boolean coincideNombre = nombreB.isEmpty() || f.getNombre().toLowerCase().contains(nombreB);
            if (coincideId && coincideNombre) datos.add(f);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblFuncionarioError.setText("");
        try {
            FuncionarioDTO dto=new FuncionarioDTO(txtFuncionarioId.getText(),null, txtFuncionarioNombre.getText(), txtFuncionarioTelefono.getText());
            if (editandoExistente) {
                funcionarioService.modificar(dto);
            } else {
                funcionarioService.crear(dto);
            }
            limpiarFormulario();
            cargarTodos();
        } catch (ValidacionException e) {
            lblFuncionarioError.setText(e.getMessage());
        }
    }

    @FXML
    public void onBorrar(ActionEvent event) {
        lblFuncionarioError.setText("");
        if (txtFuncionarioId.getText() == null || txtFuncionarioId.getText().isBlank()) {
            lblFuncionarioError.setText("Seleccione un funcionario de la lista para borrar.");
            return;
        }
        try {
            funcionarioService.eliminar(txtFuncionarioId.getText());
            limpiarFormulario();
            cargarTodos();
        } catch (ValidacionException e) {
            lblFuncionarioError.setText(e.getMessage());
        }
    }

    @FXML
    public void onLimpiar(ActionEvent event) {
        limpiarFormulario();
        lblFuncionarioError.setText("");
    }
    @FXML
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) tblFuncionarioListado.getScene().getWindow()).close();
    }
    @FXML
    public void onImprimir(ActionEvent event) {
        lblFuncionarioError.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de funcionarios");
        fileChooser.setInitialFileName("funcionarios.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tblFuncionarioListado.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        String[] encabezados = {"Id", "Nombre", "Telefono"};
        List<String[]> filas = new ArrayList<>();
        for (FuncionarioDTO f : datos) {
            filas.add(new String[]{f.getId(), f.getNombre(), f.getTelefono()});
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Listado de Funcionarios", encabezados, filas);
        } catch (IOException e) {
            lblFuncionarioError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void cargarTodos() {
        datos.clear();
        datos.addAll(funcionarioService.obtenerTodos());
    }

    private void limpiarFormulario() {
        txtFuncionarioId.clear();
        txtFuncionarioId.setEditable(true);
        txtFuncionarioNombre.clear();
        txtFuncionarioTelefono.clear();
        tblFuncionarioListado.getSelectionModel().clearSelection();
        editandoExistente = false;
    }
}