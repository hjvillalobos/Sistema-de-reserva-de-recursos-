package una.eif206.reservas.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;

import java.io.IOException;
import java.net.URL;

public class PrincipalController {

    @FXML private Label lblBienvenida;
    @FXML private Button btnCategorias;
    @FXML private Button btnFuncionarios;
    @FXML private Button btnReservas;

    private Usuario usuarioActual;

    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        lblBienvenida.setText("Bienvenido, " + usuario.getId() + " (" + usuario.getRol() + ")");

        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            btnCategorias.setVisible(true);
            btnCategorias.setManaged(true);
            btnFuncionarios.setVisible(true);
            btnFuncionarios.setManaged(true);
        }

        if (usuario.getRol() == Rol.FUNCIONARIO) {
            btnReservas.setVisible(true);
            btnReservas.setManaged(true);
        }
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
            stage.setScene(new Scene(root));
            stage.setTitle("Mis Reservas");
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista", e.getMessage());
        }
    }

    // Para vistas que NO necesitan recibir datos del usuario (Categorías, Funcionarios)
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
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista", e.getMessage());
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

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Reservas");
        } catch (IOException e) {
            mostrarError("No se pudo volver al login", e.getMessage());
        }
    }
}