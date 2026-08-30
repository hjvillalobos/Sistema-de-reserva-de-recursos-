package una.eif206.reservas.controlador;

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
import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.servicio.FuncionarioService;
import una.eif206.reservas.servicio.ValidacionException;
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
    @FXML private TableView<Funcionario> tblFuncionarioListado;
    @FXML private TableColumn<Funcionario, String> colFuncionarioId;
    @FXML private TableColumn<Funcionario, String> colFuncionarioNombre;
    @FXML private TableColumn<Funcionario, String> colFuncionarioTelefono;
    @FXML private Label lblFuncionarioError;

    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final ObservableList<Funcionario> datos = FXCollections.observableArrayList();
    private boolean editandoExistente = false;

    @FXML
    public void initialize() {
        colFuncionarioId.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("id"));
        colFuncionarioNombre.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("nombre"));
        colFuncionarioTelefono.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("telefono"));
        tblFuncionarioListado.setItems(datos);

        tblFuncionarioListado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Funcionario>() {
            @Override
            public void changed(ObservableValue<? extends Funcionario> observable,
                                Funcionario valorAnterior, Funcionario valorNuevo) {
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
        List<Funcionario> resultado = funcionarioService.buscar(txtFuncionarioBusquedaId.getText(), txtFuncionarioBusquedaNombre.getText());
        for (Funcionario f : resultado) {
            datos.add(f);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblFuncionarioError.setText("");
        try {
            if (editandoExistente) {
                funcionarioService.modificar(txtFuncionarioId.getText(), txtFuncionarioNombre.getText(), txtFuncionarioTelefono.getText());
            } else {
                funcionarioService.crear(txtFuncionarioId.getText(), txtFuncionarioNombre.getText(), txtFuncionarioTelefono.getText());
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
        for (Funcionario f : datos) {
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
        List<Funcionario> lista = funcionarioService.listarTodos();
        for (Funcionario f : lista) {
            datos.add(f);
        }
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