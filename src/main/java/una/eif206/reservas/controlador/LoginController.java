// controlador/LoginController.java
package una.eif206.reservas.controlador;

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
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.servicio.CredencialesInvalidasException;
import una.eif206.reservas.servicio.LoginService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtId;
    @FXML private PasswordField txtClave;
    @FXML private Label lblError;
    @FXML private Button btnIngresar;
    @FXML private Button btnCancelar;

    private final LoginService loginService = new LoginService();

    @FXML
    public void onIngresar(ActionEvent event) {
        lblError.setText("");
        try {
            Usuario usuario = loginService.autenticar(txtId.getText(), txtClave.getText());
            abrirVistaPrincipal(usuario);
        } catch (CredencialesInvalidasException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    @FXML
    public void onCambiarClave(ActionEvent event) {
        lblError.setText("");
        try {
            Usuario usuario = loginService.autenticar(txtId.getText(), txtClave.getText());
            abrirVistaCambiarClave(usuario);
        } catch (CredencialesInvalidasException e) {
            lblError.setText("Ingrese un id/clave válidos antes de cambiarla.");
        }
    }

    private void abrirVistaPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/una/eif206/reservas/fxml/principal.fxml"));
            Parent root = loader.load();
            ((PrincipalController) loader.getController()).inicializar(usuario);

            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Reservas - " + usuario.getId() + " (" + usuario.getRol() + ")");
        } catch (IOException e) {
            lblError.setText("No se pudo cargar la vista principal.");
        }
    }

    private void abrirVistaCambiarClave(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/una/eif206/reservas/fxml/cambiarClave.fxml"));
            Parent root = loader.load();
            ((CambiarClaveController) loader.getController()).inicializar(usuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cambiar Clave");
            stage.showAndWait();
        } catch (IOException e) {
            lblError.setText("No se pudo abrir la ventana de cambio de clave.");
        }
    }
}