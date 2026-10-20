package una.eif206.reservas.controladores;

import una.eif206.reservas.cliente.ServiciosCliente;
import una.eif206.reservas.cliente.TareasUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.UsuarioSesionDTO;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.api.LoginApi;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private TextField txtLoginId;
    @FXML private PasswordField txtLoginClave;
    @FXML private Label lblLoginError;
    @FXML private Button btnLoginIngresar;
    @FXML private Button btnLoginCancelar;

    private final LoginApi loginService = ServiciosCliente.obtener().login();

    @FXML
    public void onIngresar(ActionEvent event) {
        if (TareasUI.ocupada(lblLoginError)) return;
        String id = txtLoginId.getText();
        String clave = txtLoginClave.getText();
        btnLoginIngresar.setDisable(true);
        TareasUI.ejecutar(lblLoginError,
                () -> loginService.autenticarSesion(id, clave),
                usuario -> { txtLoginClave.clear(); abrirVistaPrincipal(usuario); },
                () -> btnLoginIngresar.setDisable(false));
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ServiciosCliente.cerrar();
        ((Stage) btnLoginCancelar.getScene().getWindow()).close();
    }

    @FXML
    public void onCambiarClave(ActionEvent event) {
        if (TareasUI.ocupada(lblLoginError)) return;
        String id = txtLoginId.getText();
        String clave = txtLoginClave.getText();
        btnLoginIngresar.setDisable(true);
        TareasUI.ejecutar(lblLoginError,
                () -> loginService.autenticarSesion(id, clave),
                usuario -> { txtLoginClave.clear(); abrirVistaCambiarClave(); },
                () -> btnLoginIngresar.setDisable(false));
    }

    private void abrirVistaPrincipal(UsuarioSesionDTO usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/una/eif206/reservas/fxml/principal.fxml"));
            Parent root = loader.load();

            PrincipalController controller = loader.getController();
            controller.inicializar(usuario);

            Stage stage = (Stage) txtLoginId.getScene().getWindow();
            Scene scene = new Scene(root);


            URL cssUrl = getClass().getResource("/una/eif206/reservas/fxml/css/estilos.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Sistema de Reservas - Menú Principal");
            stage.show();
        } catch (IOException e) {
            lblLoginError.setText("Error al cargar la vista principal: " + e.getMessage());
        }
    }

    private void abrirVistaCambiarClave() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/una/eif206/reservas/fxml/cambiarClave.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(txtLoginId.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Cambiar Clave");
            stage.showAndWait();
            TareasUI.ejecutar(lblLoginError, () -> { loginService.cerrarSesion(); return null; }, ignorado -> { });
        } catch (IOException e) {
            lblLoginError.setText("No se pudo abrir la ventana de cambio de clave.");
        }
    }
}