package una.eif206.reservas.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;

import java.io.IOException;
import java.net.URL;

public class PrincipalController {

    @FXML private Label lblPrincipalBienvenida;
    @FXML private Button btnPrincipalCategorias;
    @FXML private Button btnPrincipalFuncionarios;
    @FXML private Button btnPrincipalRecursos;
    @FXML private Button btnPrincipalReservas;
    @FXML private Button btnProgramacion;
    @FXML private Button btnEstadisticas;
    @FXML private Button btnCalendarizacion;

    private UsuarioDTO usuarioActual;

    private final String RUTA_CSS = "/una/eif206/reservas/fxml/css/estilos.css";

    public void inicializar(UsuarioDTO usuario) {
        this.usuarioActual = usuario;
        lblPrincipalBienvenida.setText("OPERADOR: " + usuario.getId().toUpperCase() + " [" + usuario.getRol().toString().toUpperCase() + "]");

        if (usuario.getRol() == RolDTO.ADMINISTRADOR) {
            btnPrincipalCategorias.setVisible(true);
            btnPrincipalCategorias.setManaged(true);
            btnPrincipalFuncionarios.setVisible(true);
            btnPrincipalFuncionarios.setManaged(true);
            btnPrincipalRecursos.setVisible(true);
            btnPrincipalRecursos.setManaged(true);
        }

        if (usuario.getRol() == RolDTO.FUNCIONARIO) {
            btnPrincipalReservas.setVisible(true);
            btnPrincipalReservas.setManaged(true);
        }
        if(usuario.getRol() == RolDTO.ADMINISTRADOR||usuario.getRol() == RolDTO.FUNCIONARIO){
            btnCalendarizacion.setVisible(true);
            btnCalendarizacion.setManaged(true);
            btnProgramacion.setVisible(true);
            btnProgramacion.setManaged(true);
            btnEstadisticas.setVisible(true);
            btnEstadisticas.setManaged(true);
        }
    }

    @FXML
    public void onAbrirProgramacion(ActionEvent event){
        abrirVentanaSimple("/una/eif206/reservas/fxml/programacion.fxml","Programación de Actividades");
    }

    @FXML
    public void onAbrirEstadisticas(ActionEvent event){
        abrirVentanaSimple("/una/eif206/reservas/fxml/estadisticas.fxml", "Estadísticas");
    }

    @FXML
    public void onAbrirCalendarizacion(ActionEvent event){
        abrirVentanaSimple("/una/eif206/reservas/fxml/calendarizacionReservas.fxml", "Calendarizacion de Recursos");
    }

    @FXML
    public void onAbrirCategorias(ActionEvent event) {
        abrirVentanaSimple("/una/eif206/reservas/fxml/categorias.fxml", "Lista de Categorías");
    }

    @FXML
    public void onAbrirFuncionarios(ActionEvent event) {
        abrirVentanaSimple("/una/eif206/reservas/fxml/funcionarios.fxml", "Lista de Funcionarios");
    }
    @FXML
    public void onAbrirRecursos(ActionEvent event) {
        abrirVentanaSimple("/una/eif206/reservas/fxml/recursos.fxml", "Lista de Recursos");
    }
    @FXML
    public void onAbrirReservas(ActionEvent event) {
        String rutaFxml = "/una/eif206/reservas/fxml/reservas.fxml";
        URL recurso = getClass().getResource(rutaFxml);

        if (recurso == null) {
            mostrarError("No se encontró la vista", "No se pudo ubicar el archivo: " + rutaFxml);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            ReservaController controller = loader.getController();
            controller.inicializar(usuarioActual);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            aplicarEstilos(scene);

            stage.setScene(scene);
            stage.setTitle("Mis Reservas");
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista", e.getMessage());
        }
    }

    private void abrirVentanaSimple(String rutaFxml, String titulo) {
        URL recurso = getClass().getResource(rutaFxml);

        if (recurso == null) {
            mostrarError("No se encontró la vista", "No se pudo ubicar el archivo: " + rutaFxml);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            aplicarEstilos(scene);

            Stage ventanaPrincipal = (Stage) lblPrincipalBienvenida.getScene().getWindow();
            stage.initOwner(ventanaPrincipal);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista", e.getMessage());
        }
    }
    private void aplicarEstilos(Scene scene) {
        URL cssUrl = getClass().getResource(RUTA_CSS);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Advertencia: No se encontró el archivo CSS en la ruta: " + RUTA_CSS);
        }
    }

    private void mostrarError(String encabezado, String detalle) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(encabezado);
        alerta.setContentText(detalle);
        alerta.showAndWait();
    }

    @FXML
    public void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/una/eif206/reservas/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lblPrincipalBienvenida.getScene().getWindow();
            Scene scene = new Scene(root);
            aplicarEstilos(scene);

            stage.setScene(scene);
            stage.setTitle("Sistema de Reservas");
        } catch (IOException e) {
            mostrarError("No se pudo volver al login", e.getMessage());
        }
    }
}