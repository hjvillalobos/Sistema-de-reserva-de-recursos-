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

    @FXML private ComboBox<Categoria> cmbRecursoFiltroCategoria;
    @FXML private TextField txtRecursoFiltroDescripcion;
    @FXML private TextField txtRecursoId;
    @FXML private ComboBox<Categoria> cmbRecursoCategoria;
    @FXML private TextField txtRecursoDescripcion;
    @FXML private TableView<Recurso> tblRecursoListado;
    @FXML private TableColumn<Recurso, String> colRecursoId;
    @FXML private TableColumn<Recurso, String> colRecursoCategoria;
    @FXML private TableColumn<Recurso, String> colRecursoDescripcion;
    @FXML private Label lblRecursoError;

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
        cmbRecursoFiltroCategoria.setItems(categoriasParaFiltro);

        cmbRecursoCategoria.setItems(FXCollections.observableArrayList(categoriasDisponibles));

        colRecursoId.setCellValueFactory(new PropertyValueFactory<Recurso, String>("id"));
        colRecursoDescripcion.setCellValueFactory(new PropertyValueFactory<Recurso, String>("descripcion"));
        colRecursoCategoria.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Recurso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Recurso, String> data) {
                return new SimpleStringProperty(obtenerDescripcionCategoria(data.getValue().getCategoriaId()));
            }
        });

        tblRecursoListado.setItems(datos);

        tblRecursoListado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Recurso>() {
            @Override
            public void changed(ObservableValue<? extends Recurso> observable,
                                Recurso valorAnterior, Recurso valorNuevo) {
                if (valorNuevo != null) {
                    txtRecursoId.setText(valorNuevo.getId());
                    txtRecursoId.setEditable(false);
                    txtRecursoDescripcion.setText(valorNuevo.getDescripcion());
                    seleccionarCategoriaEnCombo(cmbRecursoCategoria, valorNuevo.getCategoriaId());
                    editandoExistente = true;
                }
            }
        });

        cargarTodos();
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        datos.clear();
        String categoriaId = (cmbRecursoFiltroCategoria.getValue() == null) ? "" : cmbRecursoFiltroCategoria.getValue().getId();
        List<Recurso> resultado = recursoService.buscar(categoriaId, txtRecursoFiltroDescripcion.getText());
        for (Recurso r : resultado) {
            datos.add(r);
        }
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        lblRecursoError.setText("");

        if (cmbRecursoCategoria.getValue() == null) {
            lblRecursoError.setText("Debe seleccionar una categoría.");
            return;
        }
        String categoriaId = cmbRecursoCategoria.getValue().getId();

        try {
            if (editandoExistente) {
                recursoService.modificar(txtRecursoId.getText(), categoriaId, txtRecursoDescripcion.getText());
            } else {
                recursoService.crear(txtRecursoId.getText(), categoriaId, txtRecursoDescripcion.getText());
            }
            limpiarFormulario();
            cargarTodos();
        } catch (ValidacionException e) {
            lblRecursoError.setText(e.getMessage());
        }
    }

    @FXML
    public void onBorrar(ActionEvent event) {
        lblRecursoError.setText("");
        if (txtRecursoId.getText() == null || txtRecursoId.getText().isBlank()) {
            lblRecursoError.setText("Seleccione un recurso de la lista para borrar.");
            return;
        }
        try {
            recursoService.eliminar(txtRecursoId.getText());
            limpiarFormulario();
            cargarTodos();
        } catch (ValidacionException e) {
            lblRecursoError.setText(e.getMessage());
        }
    }

    @FXML
    public void onLimpiar(ActionEvent event) {
        limpiarFormulario();
        lblRecursoError.setText("");
    }

    @FXML
    public void onImprimir(ActionEvent event) {
        lblRecursoError.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de recursos");
        fileChooser.setInitialFileName("recursos.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        Stage stage = (Stage) tblRecursoListado.getScene().getWindow();
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
            lblRecursoError.setText("No se pudo generar el PDF: " + e.getMessage());
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
        txtRecursoId.clear();
        txtRecursoId.setEditable(true);
        txtRecursoDescripcion.clear();
        cmbRecursoCategoria.setValue(null);
        tblRecursoListado.getSelectionModel().clearSelection();
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