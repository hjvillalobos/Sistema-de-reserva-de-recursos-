package una.eif206.reservas.controlador;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.Callback;
import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.servicio.CategoriaService;
import una.eif206.reservas.servicio.RecursoService;
import una.eif206.reservas.servicio.ValidacionException;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecursoController {

    @FXML private ComboBox<Categoria> comboFiltroCategoria;
    @FXML private TextField txtFiltroDescripcion;
    @FXML private TextField txtId;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private TextField txtDescripcion;
    @FXML private TableView<Recurso> tablaRecursos;
    @FXML private TableColumn<Recurso, String> colId;
    @FXML private TableColumn<Recurso, String> colCategoria;
    @FXML private TableColumn<Recurso, String> colDescripcion;
    @FXML private Label lblError;

    private final RecursoService recursoService = new RecursoService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<Recurso> datos = FXCollections.observableArrayList();
    private List<Categoria> categoriasDisponibles = new ArrayList<>();
    private boolean editandoExistente = false;

    @FXML
    public void initialize() {
        categoriasDisponibles = categoriaService.listarTodos();

        ObservableList<Categoria> categoriasParaFiltro = FXCollections.observableArrayList();
        categoriasParaFiltro.add(null); // opción "todas"
        categoriasParaFiltro.addAll(categoriasDisponibles);
        comboFiltroCategoria.setItems(categoriasParaFiltro);

        comboCategoria.setItems(FXCollections.observableArrayList(categoriasDisponibles));

        colId.setCellValueFactory(new PropertyValueFactory<Recurso, String>("id"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Recurso, String>("descripcion"));
        colCategoria.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Recurso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Recurso, String> data) {
                return new SimpleStringProperty(obtenerDescripcionCategoria(data.getValue().getCategoriaId()));
            }
        });

        tablaRecursos.setItems(datos);

        tablaRecursos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Recurso>() {
            @Override
            public void changed(ObservableValue<? extends Recurso> observable,
                                Recurso valorAnterior, Recurso valorNuevo) {
                if (valorNuevo != null) {
                    txtId.setText(valorNuevo.getId());
                    txtId.setEditable(false);
                    txtDescripcion.setText(valorNuevo.getDescripcion());
                    seleccionarCategoriaEnCombo(comboCategoria, valorNuevo.getCategoriaId());
                    editandoExistente = true;
                }
            }
        });

        cargarTodos();
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        datos.clear();
        String categoriaId = (comboFiltroCategoria.getValue() == null) ? "" : comboFiltroCategoria.getValue().getId();
        List<Recurso> resultado = recursoService.buscar(categoriaId, txtFiltroDescripcion.getText());
        for (Recurso r : resultado) {
            datos.add(r);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblError.setText("");

        if (comboCategoria.getValue() == null) {
            lblError.setText("Debe seleccionar una categoría.");
            return;
        }
        String categoriaId = comboCategoria.getValue().getId();

        try {
            if (editandoExistente) {
                recursoService.modificar(txtId.getText(), categoriaId, txtDescripcion.getText());
            } else {
                recursoService.crear(txtId.getText(), categoriaId, txtDescripcion.getText());
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
            lblError.setText("Seleccione un recurso de la lista para borrar.");
            return;
        }
        try {
            recursoService.eliminar(txtId.getText());
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
        fileChooser.setTitle("Guardar reporte de recursos");
        fileChooser.setInitialFileName("recursos.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tablaRecursos.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        String[] encabezados = {"Id", "Categoria", "Descripcion"};
        List<String[]> filas = new ArrayList<>();
        for (Recurso r : datos) {
            filas.add(new String[]{r.getId(), obtenerDescripcionCategoria(r.getCategoriaId()), r.getDescripcion()});
        }

        try {
            ReportePdfUtil.generarReporteTabla(archivo.getAbsolutePath(), "Listado de Recursos", encabezados, filas);
        } catch (IOException e) {
            lblError.setText("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void cargarTodos() {
        datos.clear();
        List<Recurso> lista = recursoService.listarTodos();
        for (Recurso r : lista) {
            datos.add(r);
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtId.setEditable(true);
        txtDescripcion.clear();
        comboCategoria.setValue(null);
        tablaRecursos.getSelectionModel().clearSelection();
        editandoExistente = false;
    }

    private String obtenerDescripcionCategoria(String categoriaId) {
        for (Categoria c : categoriasDisponibles) {
            if (c.getId().equalsIgnoreCase(categoriaId)) {
                return c.getDescripcion();
            }
        }
        return categoriaId;
    }

    private void seleccionarCategoriaEnCombo(ComboBox<Categoria> combo, String categoriaId) {
        for (Categoria c : combo.getItems()) {
            if (c != null && c.getId().equalsIgnoreCase(categoriaId)) {
                combo.setValue(c);
                return;
            }
        }
    }
}