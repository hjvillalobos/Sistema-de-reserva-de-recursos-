package una.eif206.reservas.controladores;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.servicios.CategoriaService;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaController {

    @FXML private TextField txtCategoriaBusquedaDescripcion;
    @FXML private TextField txtCategoriaId;
    @FXML private TextField txtCategoriaDescripcion;
    @FXML private TableView<CategoriaDTO> tblCategoriaListado;
    @FXML private TableColumn<CategoriaDTO, String> colCategoriaId;
    @FXML private TableColumn<CategoriaDTO, String> colCategoriaDescripcion;
    @FXML private Label lblCategoriaError;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<CategoriaDTO> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCategoriaId.setCellValueFactory(new PropertyValueFactory<CategoriaDTO, String>("id"));
        colCategoriaDescripcion.setCellValueFactory(new PropertyValueFactory<CategoriaDTO, String>("descripcion"));
        tblCategoriaListado.setItems(datos);

        tblCategoriaListado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CategoriaDTO>() {
            @Override
            public void changed(ObservableValue<? extends CategoriaDTO> observable,
                                CategoriaDTO valorAnterior, CategoriaDTO valorNuevo) {
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
        List<CategoriaDTO> resultado = categoriaService.buscarPorDescripcion(txtCategoriaBusquedaDescripcion.getText());
       datos.addAll(resultado);
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblCategoriaError.setText("");
        try {
            if (txtCategoriaId.getText() == null || txtCategoriaId.getText().isBlank()) {
                categoriaService.crear(new CategoriaDTO(null, txtCategoriaDescripcion.getText()));
            } else {
                categoriaService.modificar(new CategoriaDTO(txtCategoriaId.getText(), txtCategoriaDescripcion.getText()));
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
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) tblCategoriaListado.getScene().getWindow()).close();
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
        for (CategoriaDTO c : datos) {
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
        List<CategoriaDTO> lista = categoriaService.listarTodos();
        for (CategoriaDTO c : lista) {
            datos.add(c);
        }
    }

    private void limpiarFormulario() {
        txtCategoriaId.clear();
        txtCategoriaDescripcion.clear();
        tblCategoriaListado.getSelectionModel().clearSelection();
    }
}
