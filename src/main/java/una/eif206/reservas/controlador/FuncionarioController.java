// controlador/FuncionarioController.java
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

    @FXML private TextField txtBusquedaId;
    @FXML private TextField txtBusquedaNombre;
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TableView<Funcionario> tablaFuncionarios;
    @FXML private TableColumn<Funcionario, String> colId;
    @FXML private TableColumn<Funcionario, String> colNombre;
    @FXML private TableColumn<Funcionario, String> colTelefono;
    @FXML private Label lblError;

    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final ObservableList<Funcionario> datos = FXCollections.observableArrayList();
    private boolean editandoExistente = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("telefono"));
        tablaFuncionarios.setItems(datos);

        tablaFuncionarios.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Funcionario>() {
            @Override
            public void changed(ObservableValue<? extends Funcionario> observable,
                                Funcionario valorAnterior, Funcionario valorNuevo) {
                if (valorNuevo != null) {
                    txtId.setText(valorNuevo.getId());
                    txtId.setEditable(false);
                    txtNombre.setText(valorNuevo.getNombre());
                    txtTelefono.setText(valorNuevo.getTelefono());
                    editandoExistente = true;
                }
            }
        });

        cargarTodos();
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        datos.clear();
        List<Funcionario> resultado = funcionarioService.buscar(txtBusquedaId.getText(), txtBusquedaNombre.getText());
        for (Funcionario f : resultado) {
            datos.add(f);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblError.setText("");
        try {
            if (editandoExistente) {
                funcionarioService.modificar(txtId.getText(), txtNombre.getText(), txtTelefono.getText());
            } else {
                funcionarioService.crear(txtId.getText(), txtNombre.getText(), txtTelefono.getText());
            }
            limpiarFormulario();
            cargarTodos();
        } catch (ValidacionException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    public void onBorrar(ActionEvent event) {
        lblError.setText("");
        if (txtId.getText() == null || txtId.getText().isBlank()) {
            lblError.setText("Seleccione un funcionario de la lista para borrar.");
            return;
        }
        try {
            funcionarioService.eliminar(txtId.getText());
            limpiarFormulario();
            cargarTodos();
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
    public void onImprimir(ActionEvent event) {
        lblError.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de funcionarios");
        fileChooser.setInitialFileName("funcionarios.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tablaFuncionarios.getScene().getWindow();
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
            lblError.setText("No se pudo generar el PDF: " + e.getMessage());
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
        txtId.clear();
        txtId.setEditable(true);
        txtNombre.clear();
        txtTelefono.clear();
        tablaFuncionarios.getSelectionModel().clearSelection();
        editandoExistente = false;
    }
}