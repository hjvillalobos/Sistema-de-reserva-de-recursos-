package una.eif206.reservas.controladores;

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
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.servicios.LoginService;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private TextField txtLoginId;
    @FXML private PasswordField txtLoginClave;
    @FXML private Label lblLoginError;
    @FXML private Button btnLoginIngresar;
    @FXML private Button btnLoginCancelar;

    private final LoginService loginService = new LoginService();

    @FXML
    public void onIngresar(ActionEvent event) {
        lblLoginError.setText("");
        try {
            UsuarioDTO usuario = loginService.autenticar(txtLoginId.getText(), txtLoginClave.getText());
            abrirVistaPrincipal(usuario);
        } catch (ValidacionException e) {
            lblLoginError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ((Stage) btnLoginCancelar.getScene().getWindow()).close();
    }

    @FXML
    public void onCambiarClave(ActionEvent event) {
        lblLoginError.setText("");
        try {
            UsuarioDTO usuario = loginService.autenticar(txtLoginId.getText(), txtLoginClave.getText());
            abrirVistaCambiarClave(usuario);
        } catch (ValidacionException e) {
            lblLoginError.setText("Ingrese un id/clave válidos antes de cambiarla.");
        }
    }

    private void abrirVistaPrincipal(UsuarioDTO usuario) {
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

    private void abrirVistaCambiarClave(UsuarioDTO usuario) {
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
            lblLoginError.setText("No se pudo abrir la ventana de cambio de clave.");
        }
    }
}