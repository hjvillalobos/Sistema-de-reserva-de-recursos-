// controlador/CategoriaController.java
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
import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.servicio.CategoriaService;
import una.eif206.reservas.servicio.ValidacionException;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaController {

    @FXML private TextField txtBusquedaDescripcion;
    @FXML private TextField txtId;
    @FXML private TextField txtDescripcion;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, String> colId;
    @FXML private TableColumn<Categoria, String> colDescripcion;
    @FXML private Label lblError;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<Categoria> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<Categoria, String>("id"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Categoria, String>("descripcion"));
        tablaCategorias.setItems(datos);

        tablaCategorias.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categoria>() {
            @Override
            public void changed(ObservableValue<? extends Categoria> observable,
                                Categoria valorAnterior, Categoria valorNuevo) {
                if (valorNuevo != null) {
                    txtId.setText(valorNuevo.getId());
                    txtDescripcion.setText(valorNuevo.getDescripcion());
                }
            }
        });

        cargarTodas();
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        datos.clear();
        List<Categoria> resultado = categoriaService.buscarPorDescripcion(txtBusquedaDescripcion.getText());
        for (Categoria c : resultado) {
            datos.add(c);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblError.setText("");
        try {
            if (txtId.getText() == null || txtId.getText().isBlank()) {
                categoriaService.crear(txtDescripcion.getText());
            } else {
                categoriaService.modificar(txtId.getText(), txtDescripcion.getText());
            }
            limpiarFormulario();
            cargarTodas();
        } catch (ValidacionException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    public void onBorrar(ActionEvent event) {
        lblError.setText("");
        if (txtId.getText() == null || txtId.getText().isBlank()) {
            lblError.setText("Seleccione una categoría de la lista para borrar.");
            return;
        }
        try {
            categoriaService.eliminar(txtId.getText());
            limpiarFormulario();
            cargarTodas();
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
        fileChooser.setTitle("Guardar reporte de categorías");
        fileChooser.setInitialFileName("categorias.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tablaCategorias.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        String[] encabezados = {"Id", "Descripcion"};
        List<String[]> filas = new ArrayList<>();
        for (Categoria c : datos) {
            filas.add(new String[]{c.getId(), c.getDescripcion()});
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Listado de Categorías", encabezados, filas);
        } catch (IOException e) {
            lblError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void cargarTodas() {
        datos.clear();
        List<Categoria> lista = categoriaService.listarTodos();
        for (Categoria c : lista) {
            datos.add(c);
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtDescripcion.clear();
        tablaCategorias.getSelectionModel().clearSelection();
    }
}
