package una.eif206.reservas.controladores;

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
import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.servicios.CategoriaService;
import una.eif206.reservas.servicios.RecursoServicio;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.util.ReportePdfUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecursoController {

    @FXML private ComboBox<CategoriaDTO> cmbRecursoFiltroCategoria;
    @FXML private TextField txtRecursoFiltroDescripcion;
    @FXML private TextField txtRecursoId;
    @FXML private ComboBox<CategoriaDTO> cmbRecursoCategoria;
    @FXML private TextField txtRecursoDescripcion;
    @FXML private TableView<RecursoDTO> tblRecursoListado;
    @FXML private TableColumn<RecursoDTO, String> colRecursoId;
    @FXML private TableColumn<RecursoDTO, String> colRecursoCategoria;
    @FXML private TableColumn<RecursoDTO, String> colRecursoDescripcion;
    @FXML private Label lblRecursoError;

    private final RecursoServicio recursoService = new RecursoServicio();
    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<RecursoDTO> datos = FXCollections.observableArrayList();
    private List<CategoriaDTO> categoriasDisponibles = new ArrayList<>();
    private boolean editandoExistente = false;

    @FXML
    public void initialize() {
        categoriasDisponibles = categoriaService.listarTodos();

        ObservableList<CategoriaDTO> categoriasParaFiltro = FXCollections.observableArrayList();
        categoriasParaFiltro.add(null);
        categoriasParaFiltro.addAll(categoriasDisponibles);
        cmbRecursoFiltroCategoria.setItems(categoriasParaFiltro);

        cmbRecursoCategoria.setItems(FXCollections.observableArrayList(categoriasDisponibles));

        colRecursoId.setCellValueFactory(new PropertyValueFactory<RecursoDTO, String>("id"));
        colRecursoDescripcion.setCellValueFactory(new PropertyValueFactory<RecursoDTO, String>("descripcion"));
        colRecursoCategoria.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RecursoDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RecursoDTO, String> data) {
                return new SimpleStringProperty(obtenerDescripcionCategoria(data.getValue().getCategoriaId()));
            }
        });

        tblRecursoListado.setItems(datos);

        tblRecursoListado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RecursoDTO>() {
            @Override
            public void changed(ObservableValue<? extends RecursoDTO> observable,
                                RecursoDTO valorAnterior, RecursoDTO valorNuevo) {
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
        String descripcionB = txtRecursoFiltroDescripcion.getText() == null ? "" : txtRecursoFiltroDescripcion.getText().toLowerCase();

        for (RecursoDTO r : recursoService.obtenerTodos()) {
            boolean coincideCat = categoriaId.isEmpty() || r.getCategoriaId().equalsIgnoreCase(categoriaId);
            boolean coincideDesc = descripcionB.isEmpty() || r.getDescripcion().toLowerCase().contains(descripcionB);
            if (coincideCat && coincideDesc) datos.add(r);
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
            RecursoDTO dto = new RecursoDTO(txtRecursoId.getText(), categoriaId, txtRecursoDescripcion.getText());
            if (editandoExistente) {
                recursoService.modificar(dto);
            } else {
                recursoService.crear(dto);
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
        recursoService.eliminar(txtRecursoId.getText());
        limpiarFormulario();
        cargarTodos();
    }

    @FXML
    public void onLimpiar(ActionEvent event) {
        limpiarFormulario();
        lblRecursoError.setText("");
    }
    @FXML
    public void onVolverPrincipal(ActionEvent event) {
        ((Stage) tblRecursoListado.getScene().getWindow()).close();
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
        for (RecursoDTO r : datos) {
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
        datos.addAll(recursoService.obtenerTodos());
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
        for (CategoriaDTO c : categoriasDisponibles) {
            if (c.getId().equalsIgnoreCase(categoriaId)) {
                return c.getDescripcion();
            }
        }
        return categoriaId;
    }

    private void seleccionarCategoriaEnCombo(ComboBox<CategoriaDTO> combo, String categoriaId) {
        for (CategoriaDTO c : combo.getItems()) {
            if (c != null && c.getId().equalsIgnoreCase(categoriaId)) {
                combo.setValue(c);
                return;
            }
        }
    }
}