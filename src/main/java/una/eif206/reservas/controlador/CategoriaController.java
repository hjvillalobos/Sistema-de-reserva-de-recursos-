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

    @FXML private TextField txtCategoriaBusquedaDescripcion;
    @FXML private TextField txtCategoriaId;
    @FXML private TextField txtCategoriaDescripcion;
    @FXML private TableView<Categoria> tblCategoriaListado;
    @FXML private TableColumn<Categoria, String> colCategoriaId;
    @FXML private TableColumn<Categoria, String> colCategoriaDescripcion;
    @FXML private Label lblCategoriaError;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<Categoria> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCategoriaId.setCellValueFactory(new PropertyValueFactory<Categoria, String>("id"));
        colCategoriaDescripcion.setCellValueFactory(new PropertyValueFactory<Categoria, String>("descripcion"));
        tblCategoriaListado.setItems(datos);

        tblCategoriaListado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categoria>() {
            @Override
            public void changed(ObservableValue<? extends Categoria> observable,
                                Categoria valorAnterior, Categoria valorNuevo) {
                if (valorNuevo != null) {
                    txtCategoriaId.setText(valorNuevo.getId());
                    txtCategoriaDescripcion.setText(valorNuevo.getDescripcion());
                }
            }
        });

        cargarTodas();
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        datos.clear();
        List<Categoria> resultado = categoriaService.buscarPorDescripcion(txtCategoriaBusquedaDescripcion.getText());
        for (Categoria c : resultado) {
            datos.add(c);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblCategoriaError.setText("");
        try {
            if (txtCategoriaId.getText() == null || txtCategoriaId.getText().isBlank()) {
                categoriaService.crear(txtCategoriaDescripcion.getText());
            } else {
                categoriaService.modificar(txtCategoriaId.getText(), txtCategoriaDescripcion.getText());
            }
            limpiarFormulario();
            cargarTodas();
        } catch (ValidacionException e) {
            lblCategoriaError.setText(e.getMessage());
        }
    }

    @FXML
    public void onBorrar(ActionEvent event) {
        lblCategoriaError.setText("");
        if (txtCategoriaId.getText() == null || txtCategoriaId.getText().isBlank()) {
            lblCategoriaError.setText("Seleccione una categoría de la lista para borrar.");
            return;
        }
        try {
            categoriaService.eliminar(txtCategoriaId.getText());
            limpiarFormulario();
            cargarTodas();
        } catch (ValidacionException e) {
            lblCategoriaError.setText(e.getMessage());
        }
    }

    @FXML
    public void onLimpiar(ActionEvent event) {
        limpiarFormulario();
        lblCategoriaError.setText("");
    }

    @FXML
    public void onImprimir(ActionEvent event) {
        lblCategoriaError.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de categorías");
        fileChooser.setInitialFileName("categorias.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tblCategoriaListado.getScene().getWindow();
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
            lblCategoriaError.setText("No se pudo generar el PDF: " + e.getMessage());
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
        txtCategoriaId.clear();
        txtCategoriaDescripcion.clear();
        tblCategoriaListado.getSelectionModel().clearSelection();
    }
}